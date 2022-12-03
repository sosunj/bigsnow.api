package kr.co.bigsnow.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.bigsnow.api.service.CommonService;
import kr.co.bigsnow.api.util.CtrlUtil;
import kr.co.bigsnow.core.controller.StandardController;
import kr.co.bigsnow.core.util.CommonUtil;
import kr.co.bigsnow.core.util.CoreConst;
import kr.co.bigsnow.core.util.FileManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/lecture", produces = MediaType.APPLICATION_JSON_VALUE)
@SuppressWarnings({"unchecked", "rawtypes"})
public class LectureController extends StandardController {

	/**
	 * TransactionManager
	 */
	@Autowired
	protected PlatformTransactionManager txManager;

	@Autowired
	protected CommonService commonService;

	/**
	 * 과목 SelectBox 조회
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
    @GetMapping("/subjectSelectBoxList")
    @Operation(summary = "과목 SelectBox 조회", description = "과목 SelectBox 조회")
    @Parameters({
    		@Parameter(name = "sbj_grp_cd", required = false, description = "과목 그룹코드", schema = @Schema(implementation = String.class) )
    })
    public Map<String, Object>  subjectSelectBoxList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException
    {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            //  CommonUtil.setPageParam(mapReq); // Paging 값 세팅
            List lstRs = dbSvc.dbList("lecture.subjectSelectBoxList" , mapReq);

            if ( lstRs != null ) {
               mapRlt.put("list" ,    lstRs );
               mapRlt.put("count" , lstRs.size() );
            }
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

	/**
	 * 강좌 목록를 조회
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
    @PostMapping("/subjectList")
    @Operation(summary = "메뉴 : 강좌 관리 > 강좌 목록", description = "강좌 목록를 조회합니다.")
    @Parameters({
    		@Parameter(name = "cust_nm", required = false, description = "기관명", schema = @Schema(implementation = String.class) )
    	,   @Parameter(name = "cust_no", required = false, description = "기관번호", schema = @Schema(implementation = String.class) )
    	,	@Parameter(name = "lec_nm" , required = false, description = "강좌명", schema = @Schema(implementation = String.class) )
	    ,   @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class))
	    ,   @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))
    })
    public Map<String, Object>  subjectList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException
    {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            mapReq.put("user_grp_cd"   , CommonUtil.nvlMap(mapReq, "token_user_grp_cd")   );
            mapReq.put("token_cust_no" , CommonUtil.nvlMap(mapReq, "token_cust_no")   );
            CommonUtil.setPageParam(mapReq); // Paging 값 세팅

            mapRlt.put("list" , dbSvc.dbList("lecture.subjectList" , mapReq));
            mapRlt.put("count", dbSvc.dbInt ("lecture.subjectCount", mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 강좌의 상세정보를 조회
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/subjectDetail")
    @Operation(summary = "메뉴 : 강좌 관리 > 강좌 상세", description = "강좌의 상세정보를 조회합니다.")
	@Parameters({
		@Parameter(name = "sbj_no", required = true, description = "강좌 번호", schema = @Schema(implementation = String.class))
	})
    public Map<String, Object> subjectDetail(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            mapRlt.put("detail" , dbSvc.dbDetail("lecture.subjectDetail" , mapReq));

            mapReq.put("ref_pk", CommonUtil.nvlMap(mapReq, "sbj_no") );
            mapReq.put("ref_nm", "TB_SUBJECT" );
            mapReq.put("file_gbn" , "data"       );

            mapRlt.put("fileList" , dbSvc.dbList("common.fileList" , mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 강좌 삭제
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/subjectDelete")
	@Operation(summary = "메뉴 : 강좌 관리 > 강좌 [강좌 삭제]", description = "강좌흘 삭제한다.(use_yn 값을 N으로 세팅한다.) " )
	@Parameters({
		@Parameter(name = "sbj_no", required = true, description = "강좌 번호", schema = @Schema(implementation = String.class))
	})
	public  Map<String, Object>  subjectDelete(HttpServletRequest request, HttpServletResponse response,
			RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {
		Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

		int deleteRow = 0;
        Exception rstEx                 = null;
        try{
            if (!"".equals(CommonUtil.nvlMap(mapReq, "sbj_no"))) {
                deleteRow = dbSvc.dbUpdate("lecture.updateSubjectUseYn", mapReq);
                CtrlUtil.settingRst(mapRlt, 200, "success", "Y");
            } else {
                CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
            }
        }catch (Exception ex){
            rstEx = ex;
            mapRlt = buildResult(mapRlt, mapReq, rstEx, request);
        }

        return mapRlt;
    }

    /**
     * 강좌를 등록
     * @param request
     * @param response
     * @param requestFile
     * @return
     * @throws Exception
     */
	@Operation(summary = "강좌를 등록한다.", description = "강좌를 등록한다" )
	@Parameters({
		   @Parameter(name = "cust_no", required = true, description = "고객 번호", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "sbj_grp_cd", required = true, description = "강좌구분코드", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "sbj_nm", required = true, description = "강좌명", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "class_tgt_cd", required = true, description = "수강상태코드", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "prog_state_cd", required = true, description = "진행상태코드", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "sbj_dsc", required = true, description = "과목설명", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "sbj_cls_cd", required = true, description = "분류코드", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "main_img", required = false, description = "[메인 이미지] file의 Input 테그의 이름을 main_img 로 사용해 주세요[예시 <input name='main_img' type='file'>", schema = @Schema(implementation = File.class))
		,  @Parameter(name = "main_img", required = false, description = "[교재및수업자료] file의 Input 테그의 이름을 data 로 사용해 주세요[예시 <input name='data' type='file'>", schema = @Schema(implementation = File.class))
	})
    @RequestMapping(value = "/insertSubject", method = RequestMethod.POST )
    public Map<String, Object> insertSubject(HttpServletRequest request,HttpServletResponse response,MultipartHttpServletRequest requestFile) throws Exception{

		// Map<String, Object> mapReq 		= super.setRequestMap(requestFile, response); // requestEntity
		Map<String, Object> mapReq 	= CommonUtil.getRequestFileMap(request, "/upload/file/", false); // requestEntity
		Map<String, Object> mapRlt    = new HashMap();

		FileManager fileMgr = new FileManager(request, dbSvc);

		int rtnInt = 0;
        Exception rstEx                 = null;
        try{
            dbSvc.dbInsert("lecture.insertSubject", mapReq);

            mapReq.put("ref_pk",  CommonUtil.nvlMap(mapReq, "sbj_no"));  // 테이블 키
            mapReq.put("ref_nm",  "TB_SUBJECT");  // 테이블 명

            fileMgr.fileDbSave(mapReq);
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
	}

	/**
	 * 강좌를 수정
	 * @param request
	 * @param response
	 * @param requestFile
	 * @return
	 * @throws Exception
	 */
	@Operation(summary = "강좌를 수정한다.", description = "강좌를 수정한다" )
	@Parameters({
		  @Parameter(name = "sbj_no", required = true, description = "강좌 번호", schema = @Schema(implementation = String.class))
		, @Parameter(name = "cust_no", required = true, description = "고객 번호", schema = @Schema(implementation = String.class))
		, @Parameter(name = "sbj_grp_cd", required = true, description = "강좌구분코드", schema = @Schema(implementation = String.class))
		, @Parameter(name = "sbj_nm", required = true, description = "강좌명", schema = @Schema(implementation = String.class))
		, @Parameter(name = "class_tgt_cd", required = true, description = "수강상태코드", schema = @Schema(implementation = String.class))
		, @Parameter(name = "prog_state_cd", required = true, description = "진행상태코드", schema = @Schema(implementation = String.class))
		, @Parameter(name = "sbj_dsc", required = true, description = "과목설명", schema = @Schema(implementation = String.class))
		, @Parameter(name = "sbj_cls_cd", required = true, description = "분류코드", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "main_img", required = false, description = "[메인 이미지] file의 Input 테그의 이름을 main_img 로 사용해 주세요[예시 <input name='main_img' type='file'>", schema = @Schema(implementation = File.class))
		,  @Parameter(name = "main_img", required = false, description = "[교재및수업자료] file의 Input 테그의 이름을 data 로 사용해 주세요[예시 <input name='data' type='file'>", schema = @Schema(implementation = File.class))
	})
    @RequestMapping(value = "/updateSubject", method = RequestMethod.POST)
    public Map<String, Object> updateSubject(HttpServletRequest request,HttpServletResponse response,MultipartHttpServletRequest requestFile) throws Exception{

		Map<String, Object> mapReq 	= CommonUtil.getRequestFileMap(request, "/upload/file/", false); // requestEntity
		Map<String, Object> mapRlt    = new HashMap();

		FileManager fileMgr = new FileManager(request, dbSvc);

		int rtnInt = 0;
        Exception rstEx                 = null;
        try{
            dbSvc.dbUpdate("lecture.updateSubject", mapReq);

            mapReq.put("ref_pk",  CommonUtil.nvlMap(mapReq, "sbj_no"));  // 테이블 키
            mapReq.put("ref_nm",  "TB_SUBJECT");  // 테이블 명

            fileMgr.fileDbSave(mapReq);
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
	}

	/**
	 * 강좌개설 목록를 조회
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */



    @PostMapping("/lectureList")
    @Operation(summary = "메뉴 > 강좌 관리 > 강좌 개설 목록", description = "강좌개설 목록를 조회합니다.")
    @Parameters({
    		@Parameter(name = "cust_nm", required = false, description = "기관명", schema = @Schema(implementation = String.class) )
    	,   @Parameter(name = "cust_no", required = false, description = "기관번호", schema = @Schema(implementation = String.class) )
		,   @Parameter(name = "lec_year", required = false, description = "강의년도", schema = @Schema(implementation = String.class) )
    	,	@Parameter(name = "lec_nm" , required = false, description = "강좌명", schema = @Schema(implementation = String.class) )
    	,	@Parameter(name = "sbj_nm" , required = false, description = "과목명", schema = @Schema(implementation = String.class) )
    	,	@Parameter(name = "prog_state_cd" , required = false, description = "진행상태(진행:prog_state_cd=01, 종료:prog_state_cd=02)", schema = @Schema(implementation = String.class) )
		,	@Parameter(name = "teacher_no" , required = false, description = "강사번호", schema = @Schema(implementation = String.class) )
		,   @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class))
		,   @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))
    })
    public Map<String, Object>  lectureList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException
    {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            mapReq.put("user_grp_cd" , CommonUtil.nvlMap(mapReq, "token_user_grp_cd")   );
            mapReq.put("token_cust_no" , CommonUtil.nvlMap(mapReq, "token_cust_no")   );

            CommonUtil.setPageParam(mapReq); // Paging 값 세팅

            mapRlt.put("list" , dbSvc.dbList("lecture.lectureList" , mapReq));
            mapRlt.put("count", dbSvc.dbInt ("lecture.lectureCount", mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }




    /**
     * 과목조회[팝업]
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/subjectPopList")
    @Operation(summary = "메뉴 > 강좌 관리 > 과목조회[팝업]", description = "강좌 과목을 선택할 수 있도록 데이터를 조회한다.")
    @Parameters({
    		@Parameter(name = "sbj_nm" , required = false, description = "과목명", schema = @Schema(implementation = String.class) )

    })
    public Map<String, Object>  subjectPopList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException
    {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            mapReq.put("user_grp_cd" , CommonUtil.nvlMap(mapReq, "token_user_grp_cd")   );
            mapReq.put("token_cust_no" , CommonUtil.nvlMap(mapReq, "token_cust_no")   );

            mapRlt.put("list" , dbSvc.dbList("lecture.subjectList" , mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 강좌개설의 상세정보를 조회
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @Operation(summary = "메뉴 > 강좌 관리 > 강좌개설 상세", description = "강좌개설의 상세정보를 조회합니다.")
    @PostMapping("/lectureDetail")
	@Parameters({
		@Parameter(name = "lec_no", required = true, description = "강좌 번호", schema = @Schema(implementation = String.class))
	})
    public Map<String, Object> lectureDetail(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            mapRlt.put("detail" , dbSvc.dbDetail("lecture.lectureDetail" , mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 강좌 개섫흘 삭제
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
	@Operation(summary = "메뉴 > 강좌 관리 > 강좌개설 삭제", description = "강좌 개섫흘 삭제한다.(use_yn 값을 N으로 세팅한다." )
	@Parameters({
		@Parameter(name = "lec_no", required = true, description = "강좌 번호", schema = @Schema(implementation = String.class))
	})
	@RequestMapping(value = "/deleteLecture", method = RequestMethod.POST)
	public  Map<String, Object>  delete(HttpServletRequest request, HttpServletResponse response,
			RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

		Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

		int deleteRow = 0;
        Exception rstEx                 = null;
        try{
            if (!"".equals(CommonUtil.nvlMap(mapReq, "lec_no"))) {
                deleteRow = dbSvc.dbUpdate("lecture.updateLectureUseYn", mapReq);
                CtrlUtil.settingRst(mapRlt, 200, "success", "Y");
            } else {
                CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
            }

            // 수업 삭제 후 생성
            dbSvc.dbInsert("lesson.deleteLession", mapReq);
            dbSvc.dbInsert("lesson.insertLession", mapReq);
        }catch (Exception ex){
            rstEx = ex;
            mapRlt = buildResult(mapRlt, mapReq, rstEx, request);
        }
        return mapRlt;
    }

	/**
	 * 개설강좌 등록
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
	@Operation(summary = "메뉴 > 강좌 관리 > 개설 강좌를 등록한다.", description = "개설강좌에 대하여 등록한다." )
	@Parameters({
		    @Parameter(name = "sbj_no",required = false, description="과목번호"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_nm",required = false, description="강좌명  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "teacher_no", required = false, description="강사번호"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "capa_num",   required = false, description="정원"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_year",   required = false, description="년도"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_degree", required = false, description="차수"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "prog_state_cd",  required = false, description="진행상태코드(PSC)"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_fr_dt",  required = false, description="강좌시작일   "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_to_dt",  required = false, description="강좌종료일   "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_mon_yn", required = false, description="강좌요일-월  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_tue_yn", required = false, description="강좌요일-화  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_wed_yn", required = false, description="강좌요일-수  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_thu_yn", required = false, description="강좌요일-목  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_fri_yn", required = false, description="강좌요일-금  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_sat_yn", required = false, description="강좌요일-토  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_sun_yn", required = false, description="강좌요일-일  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_fr_tm",  required = false, description="강좌시작시간 "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_to_tm",  required = false, description="강좌종료시간 "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_dsc",    required = false, description="강좌설명"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "materials",  required = false, description="준비물  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_room",   required = false, description="강의실 구분  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "fee",        required = false, description="수강료  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "tot_round",  required = false, description="총 회차 "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "prog_round", required = false, description="진행 회차"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_inwon" ,  required = false, description="수강인원수"  , schema = @Schema(implementation = String.class))
	})
	@RequestMapping(value = "/insertLecture", method = RequestMethod.POST)
    public Map<String, Object> insertLecture(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
	    Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt  = new HashMap();

		int rtnInt = 0;
        Exception rstEx                 = null;
        try{
            ObjectMapper mapper = new ObjectMapper();

            mapReq.put("lec_fr_dt", CommonUtil.removeDateFormat(mapReq, "lec_fr_dt") );
            mapReq.put("lec_to_dt", CommonUtil.removeDateFormat(mapReq, "lec_to_dt") );
            mapReq.put("fee"      , CommonUtil.removeComma(mapReq, "fee") );

            dbSvc.dbInsert("lecture.insertLecture", mapReq);

            // 수업 삭제 후 생성
            mapper = new ObjectMapper();
            mapReq.put("lec_no", mapReq.get("returnKey") );
            dbSvc.dbInsert("lesson.deleteLession", mapReq);
            dbSvc.dbInsert("lesson.insertLession", mapReq);

        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
	}


	/**
	 * 개설강좌 수정
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
	@Operation(summary = "메뉴 > 강좌 관리 > 개설 강좌를 수정한다.", description = "개설강좌에 대하여 수정한다." )
	@Parameters({
		    @Parameter(name = "lec_no",required = true, description="강좌번호"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "sbj_no",required = false, description="과목번호"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_nm",required = false, description="강좌명  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "teacher_no", required = false, description="강사번호"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "capa_num",   required = false, description="정원"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_year",   required = false, description="년도"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_degree", required = false, description="차수"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "prog_state_cd",  required = false, description="진행상태코드(PSC)"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_fr_dt",  required = false, description="강좌시작일   "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_to_dt",  required = false, description="강좌종료일   "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_mon_yn", required = false, description="강좌요일-월  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_tue_yn", required = false, description="강좌요일-화  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_wed_yn", required = false, description="강좌요일-수  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_thu_yn", required = false, description="강좌요일-목  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_fri_yn", required = false, description="강좌요일-금  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_sat_yn", required = false, description="강좌요일-토  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_sun_yn", required = false, description="강좌요일-일  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_fr_tm",  required = false, description="강좌시작시간 "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_to_tm",  required = false, description="강좌종료시간 "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_dsc",required = false, description="강좌설명"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "materials",  required = false, description="준비물  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_room",   required = false, description="강의실 구분  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "fee",   required = false, description="수강료  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "tot_round",  required = false, description="총 회차 "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "prog_round", required = false, description="진행 회차"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_inwon" ,  required = false, description="수강인원수"  , schema = @Schema(implementation = String.class))
	})
	@RequestMapping(value = "/updateLecture", method = RequestMethod.POST)
    public Map<String, Object> updateLecture(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            mapReq.put("lec_fr_dt", CommonUtil.removeDateFormat(mapReq, "lec_fr_dt") );
            mapReq.put("lec_to_dt", CommonUtil.removeDateFormat(mapReq, "lec_to_dt") );
            mapReq.put("fee"      , CommonUtil.removeComma(mapReq, "fee") );

            dbSvc.dbInsert("lecture.updateLecture", mapReq);

            // 수업 삭제 후 생성
            dbSvc.dbInsert("lesson.deleteLession", mapReq);
            dbSvc.dbInsert("lesson.insertLession", mapReq);
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
	}


	/**
	 * 강사 목록조회
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
    @GetMapping("/lectureTeacherList")
    @Operation(summary = "강사관리의 강사일정 팝업 및 강좌목록에서 사용", description = "강사일정의 팝업에서 사용하는 강사 목록조회")
    @Parameters({
    		@Parameter(name = "user_no", required = false, description = "강사번호", schema = @Schema(implementation = Integer.class) )
    })
    public Map<String, Object>  lectureTeacherSchedultList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException
    {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            List lstRs = dbSvc.dbList("lecture.lectureTeacherScheduleList" , mapReq);

            mapRlt.put("list" , lstRs);

            if ( lstRs != null ) {
               mapRlt.put("count", lstRs.size());
            }
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 엑셀 업로드를 통해 강좌를 저장
     * @param request
     * @param response
     * @param requestFile
     * @return
     * @throws JsonProcessingException
     */
	@PostMapping("/insertSubjectExcelUpload")
	@Operation(summary = "메뉴: 강좌관리 > 강좌목록 목록 : 강좌를 엑셀로 등록(엑셀 등록) ", description = "엑셀 업로드를 통해 강좌를 저장한다.")
	@Parameters({
		   @Parameter(name = "up_file", required = true, description = "업로드 파일명", schema = @Schema(implementation = String.class))
	})
	public    Map<String, Object>  insertSubjectExcelUpload (HttpServletRequest  request, HttpServletResponse response , MultipartHttpServletRequest requestFile )  throws JsonProcessingException
	{
	   	Map<String, Object> mapReq 	= CommonUtil.getRequestFileMap(request, "/upload/excel/", false); // requestEntity
	 	Map<String, Object> mapRlt    = new HashMap();

	    Exception rstEx                 = null;
        try{
           FileManager fileMgr = new FileManager(request, dbSvc);

           mapReq.put(CoreConst.INPUT_DUPCHECK, "Y");
           mapReq.put("excel_header_id", CoreConst.EXCEL_HD_SUBJECT );

           mapRlt = commonService.excelUploadProc( request,  response, mapReq, fileMgr );
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
	}



}
