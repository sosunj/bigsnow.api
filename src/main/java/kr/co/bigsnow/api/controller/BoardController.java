package kr.co.bigsnow.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.bigsnow.api.util.CtrlUtil;
import kr.co.bigsnow.core.controller.StandardController;
import kr.co.bigsnow.core.util.CommonUtil;
import kr.co.bigsnow.core.util.FileManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/board", produces = MediaType.APPLICATION_JSON_VALUE)
@SuppressWarnings({"unchecked", "rawtypes"})
public class BoardController extends StandardController {

	/**
	 * TransactionManager
	 */
	@Autowired
	protected PlatformTransactionManager txManager;

	/**
	 * 공지사항을 목록을 조회
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
    @PostMapping("/noticeList")
    @Operation(summary = "메뉴:설정관리 > 공지사항 > 목록", description = "공지사항을 목록을 조회한다.")
    @Parameters({
			@Parameter(name = "ttl", required = false, description = "제목", schema = @Schema(implementation = String.class) )
		,   @Parameter(name = "reg_fr_dt", required = false, description = "등록일 기준(조회 시작일)", schema = @Schema(implementation = String.class) )
		,   @Parameter(name = "reg_to_dt", required = false, description = "등록일 기준(조회 종료일)", schema = @Schema(implementation = String.class) )
	    ,   @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class))
	    ,   @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))
    })
    public Map<String, Object>  noticeList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException
    {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt  = new HashMap();

    	mapReq.put("brd_kind", "BKD001");
    	mapRlt = boardList( request, response, mapReq );

        return mapRlt;
    }

    /**
     * 자주하는 질문을 목록 조회
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/faqList")
    @Operation(summary = "메뉴:설정관리 > 자주하는 질문 > 목록", description = "자주하는 질문을 목록을 조회한다.")
    @Parameters({
			@Parameter(name = "ttl", required = false, description = "제목", schema = @Schema(implementation = String.class) )
		,   @Parameter(name = "reg_fr_dt", required = false, description = "등록일 기준(조회 시작일)", schema = @Schema(implementation = String.class) )
		,   @Parameter(name = "reg_to_dt", required = false, description = "등록일 기준(조회 종료일)", schema = @Schema(implementation = String.class) )
	    ,   @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class))
	    ,   @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))
    })
    public Map<String, Object>  faqList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException
    {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

    	mapReq.put("brd_kind", "BKD002");
    	mapRlt = boardList( request, response, mapReq );

        return mapRlt;
    }


    /**
     * 자료실 목록을 조회
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/dataList")
    @Operation(summary = "메뉴:설정관리 > 자료실 > 목록", description = "자료실 목록을 조회한다.")
    @Parameters({
			@Parameter(name = "ttl", required = false, description = "제목", schema = @Schema(implementation = String.class) )
		,   @Parameter(name = "reg_fr_dt", required = false, description = "등록일 기준(조회 시작일)", schema = @Schema(implementation = String.class) )
		,   @Parameter(name = "reg_to_dt", required = false, description = "등록일 기준(조회 종료일)", schema = @Schema(implementation = String.class) )
	    ,   @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class))
	    ,   @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))
    })
    public Map<String, Object>  dataList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException
    {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

    	mapReq.put("brd_kind", "BKD003");
    	return boardList( request, response, mapReq );
    }

    /**
     * 게시판 조회 공통 method
     * @param request
     * @param response
     * @param mapReq
     * @return
     * @throws JsonProcessingException
     */
    private Map<String, Object>  boardList(HttpServletRequest  request, HttpServletResponse response, Map mapReq)  throws JsonProcessingException
    {
    	Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            mapRlt.put("reg_fr_dt", CommonUtil.removeDateFormat(mapReq, "reg_fr_dt"));
            mapRlt.put("reg_to_dt", CommonUtil.removeDateFormat(mapReq, "reg_to_dt"));

            CommonUtil.setPageParam(mapReq); // Paging 값 세팅

            mapRlt.put("list" , dbSvc.dbList("board.boardList" , mapReq));
            mapRlt.put("count", dbSvc.dbInt ("board.boardCount", mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 게시판의 상세 내용을 조회
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/boardDetail")
    @Operation(summary = "메뉴 : 설정관리 > 모든 게시판 > 상세", description = "모든 게시판의 상세 내용을 조회한다.")
	@Parameters({
		@Parameter(name = "brd_reg_no", required = true, description = "게시판 번호", schema = @Schema(implementation = Integer.class))
	})
    public Map<String, Object> boardDetail(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            log.error(this.getClass().getName(), "===========================================");
            log.error(this.getClass().getName(), mapReq.toString());
            log.error(this.getClass().getName(), "===========================================");

            mapRlt.put("detail" , dbSvc.dbDetail("board.boardDetail" , mapReq));

            mapReq.put("ref_pk", CommonUtil.nvlMap(mapReq, "brd_reg_no") );
            mapReq.put("ref_nm", "TB_BOARD" );

            mapRlt.put("fileList" , dbSvc.dbList("common.fileList" , mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 게시판을 삭제
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
	@RequestMapping(value = "/boardDelete", method = RequestMethod.POST)
	@Operation(summary = "메뉴 : 설정 관리 > 게시판 삭제", description = "게시판을 삭제한다.(use_yn 값을 N으로 세팅한다.)" )
	@Parameters({
		@Parameter(name = "brd_reg_no", required = true, description = "게시판번호 번호", schema = @Schema(implementation = String.class))
	})
	public  Map<String, Object>  boardDelete(HttpServletRequest request, HttpServletResponse response,
			RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

    	Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

    	int deleteRow = 0;

        Exception rstEx                 = null;
        try{
            if (!"".equals(CommonUtil.nvlMap(mapReq, "brd_reg_no"))) {
                deleteRow = dbSvc.dbUpdate("board.updateBoardUseYn", mapReq);
                CtrlUtil.settingRst(mapRlt, 200, "success", "Y");
            } else {
                CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
            }
        }catch (Exception ex){
            rstEx = ex;
        }

        if(rstEx != null) {
            mapRlt = buildResult(mapRlt, mapReq, rstEx, request);
        }
        return mapRlt;
    }


	/**
	 * 게시판을 등록
	 * @param request
	 * @param response
	 * @param requestFile
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/insertBoard", method = RequestMethod.POST )
	@Operation(summary = "게시판을 등록한다.", description = "게시판을 등록한다" )
	@Parameters({
		   @Parameter(name = "up_reg_no"    , required = true, description = "상위게시판 번호[상위게시판번호가 없는 경우 빈 값으로 보내주세요", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "brd_kind"     , required = true, description = "게시판종류[공지사항:BKD001, FAQ:BKD002, 자료실:BKD003 ]", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "ttl"          , required = true, description = "제목", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "ctnt"         , required = true, description = "내용", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "top_notice_yn", required = true, description = "상단공지여부", schema = @Schema(implementation = String.class))
        ,  @Parameter(name = "notice_type"  , required = true, description = "공지유형", schema = @Schema(implementation = String.class))

        ,  @Parameter(name = "brd_fr_dt"    , required = false, description = "시작일", schema = @Schema(implementation = String.class))
        ,  @Parameter(name = "brd_to_dt"    , required = false, description = "종료일", schema = @Schema(implementation = String.class))

		,  @Parameter(name = "use_yn"       , required = true, description = "사용여부", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "up_file"      , required = false,  description = "첨부 파일", schema = @Schema(implementation = File.class)  )
	})
    public Map<String, Object> insertBoard (HttpServletRequest  request, HttpServletResponse response , MultipartHttpServletRequest requestFile ) throws Exception{

	    Map<String, Object> mapReq 	  = CommonUtil.getRequestFileMap(request, "/upload/board/", false);
	    Map<String, Object> mapRlt    = new HashMap();

		Map<String, Object> mapUp 	  = new HashMap();
		Map<String, Object> subMap 		= null;

		boolean fileFlag 				= false;
		int rtnInt = 0;

        Exception rstEx                 = null;
        try{
            FileManager fileMgr = new FileManager(request, dbSvc);

            CommonUtil.removeDateFormat(mapReq, "brd_fr_dt");
            CommonUtil.removeDateFormat(mapReq, "brd_to_dt");

            dbSvc.dbInsert("board.insertBoard", mapReq);

            //----------------------- 파일저장 ----------------------
            mapReq.put("ref_pk", CommonUtil.nvlMap(mapReq, "brd_reg_no"));
            mapReq.put("ref_nm", "TB_BOARD"); // 테이블 명
            fileMgr.fileDbSave(mapReq);
            //----------------------- 파일저장 ----------------------

            String strBrdRegNo = CommonUtil.nvlMap(mapReq, "brd_reg_no");
            String strUpRegNo  = CommonUtil.nvlMap(mapReq, "up_reg_no");

            if (!"".equals(strUpRegNo) && !"0".equals(strUpRegNo) ) {
                Map mapParam = new HashMap();

                mapParam.put("brd_reg_no", strUpRegNo);
                mapUp = dbSvc.dbDetail("board.boardDetail", mapParam);

                mapReq.put("depth"     , ( CommonUtil.nvlMapInt(mapUp, "depth") + 1 )  );
                mapReq.put("top_reg_no", ( CommonUtil.nvlMap(mapUp, "top_reg_no")  )  );
                mapReq.put("all_reg_no", ( CommonUtil.nvlMap(mapUp, "all_reg_no") + strBrdRegNo + ","  )  );
            } else {
                mapReq.put("depth", "1" );
                mapReq.put("top_reg_no", strBrdRegNo  );
                mapReq.put("all_reg_no", strBrdRegNo + "," );
            }

            dbSvc.dbUpdate("board.insertAfterBoardUpdate", mapReq);
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
	}

	/**
	 * 게시판을 수정
	 * @param request
	 * @param response
	 * @param requestFile
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/updateBoard", method = RequestMethod.POST )
	@Operation(summary = "게시판을 수정한다.", description = "게시판을 수정한다" )
	@Parameters({
		   @Parameter(name = "brd_reg_no"   , required = true, description = "게시판 번호", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "ttl"          , required = true, description = "제목", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "ctnt"         , required = true, description = "내용", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "top_notice_yn", required = true, description = "상단공지여부", schema = @Schema(implementation = String.class))
        ,  @Parameter(name = "notice_type"  , required = true, description = "공지유형", schema = @Schema(implementation = String.class))

        ,  @Parameter(name = "brd_fr_dt"    , required = false, description = "시작일", schema = @Schema(implementation = String.class))
        ,  @Parameter(name = "brd_to_dt"    , required = false, description = "종료일", schema = @Schema(implementation = String.class))

        ,  @Parameter(name = "use_yn"       , required = true, description = "사용여부", schema = @Schema(implementation = String.class))
	    ,  @Parameter(name = "up_file"      , required = false,  description = "첨부 파일", schema = @Schema(implementation = File.class)  )
	})
    public Map<String, Object> updateBoard( HttpServletRequest  request, HttpServletResponse response , MultipartHttpServletRequest requestFile) throws Exception{

	    Map<String, Object> mapReq 	  = CommonUtil.getRequestFileMap(request, "/upload/board/", false);
		Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            FileManager fileMgr = new FileManager(request, dbSvc);

            CommonUtil.removeDateFormat(mapReq, "brd_fr_dt");
            CommonUtil.removeDateFormat(mapReq, "brd_to_dt");

            dbSvc.dbUpdate("board.updateBoard", mapReq);

            //----------------------- 파일저장 ----------------------
            mapReq.put("ref_pk", CommonUtil.nvlMap(mapReq, "brd_reg_no"));
            mapReq.put("ref_nm", "TB_BOARD"); // 테이블 명
            fileMgr.fileDbSave(mapReq);
            //----------------------- 파일저장 ----------------------
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
	}

}
