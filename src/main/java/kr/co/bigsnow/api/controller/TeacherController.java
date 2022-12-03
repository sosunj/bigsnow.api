package kr.co.bigsnow.api.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.bigsnow.api.util.CtrlUtil;
import kr.co.bigsnow.core.controller.StandardController;
import kr.co.bigsnow.core.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/teacher", produces = MediaType.APPLICATION_JSON_VALUE)
public class TeacherController extends StandardController {

	/**
	 * 강사관리
	 */
	@Autowired
	protected PlatformTransactionManager txManager;

	/**
	 * 강사 목록를 조회
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
    @PostMapping("/teacherList")
    @Operation(summary = "메뉴 : 강사 관리 > 강사 목록", description = "강사 목록를 조회합니다.")
    @Parameters({
    		@Parameter(name = "cust_nm", required = false, description = "기관명", schema = @Schema(implementation = String.class) )
    	,   @Parameter(name = "cust_no", required = false, description = "기관번호", schema = @Schema(implementation = Integer.class) )
    	,	@Parameter(name = "user_nm", required = false, description = "강사명", schema = @Schema(implementation = String.class) )
    	,	@Parameter(name = "hp_no"  , required = false, description = "연락처", schema = @Schema(implementation = String.class) )
		,   @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class))
		,   @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))
    })
    public Map<String, Object>  subjectList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException
    {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            CommonUtil.setPageParam(mapReq); // Paging 값 세팅

            mapRlt.put("list" , dbSvc.dbList("teacher.teacherList" , mapReq));
            mapRlt.put("count", dbSvc.dbInt ("teacher.teacherCount", mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 강사의 상세정보를 조회
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/teacharDetail")
    @Operation(summary = "메뉴 : 강사 관리 > 강좌 상세", description = "강사의 상세정보를 조회합니다.")
	@Parameters({
		@Parameter(name = "user_no", required = true, description = "회원(강사)번호", schema = @Schema(implementation = Integer.class))
	})
    public Map<String, Object> subjectDetail(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            mapRlt.put("detail" , dbSvc.dbDetail("teacher.tearcherDetail" , mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 강사 상세 [강사 삭제]
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/teacharDelete")
	@Operation(summary = "메뉴 : 강사 관리 > 강사 상세 [강사 삭제]", description = "강사를 삭제한다.(use_yn 값을 N으로 세팅한다." )
	@Parameters({
		@Parameter(name = "user_no", required = true, description = "강사(회원) 번호", schema = @Schema(implementation = String.class))
	})
	public  Map<String, Object>  subjectDelete(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

		Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

		int deleteRow    = 0;
        Exception rstEx  = null;
        try{
            if (!"".equals(CommonUtil.nvlMap(mapReq, "user_no"))) {
                deleteRow = dbSvc.dbUpdate("teacher.updateteacherUseYn", mapReq);
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
     *
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/tearcherLessionPopList")
    @Operation(summary = "메뉴 : 강사 관리 > 강사 목록 > 강사일정 팝업", description = "강사 일정의 팝업을 조회한다.")
    @Parameters({
    	 	@Parameter(name = "user_no"  , required = false, description = "강사(회원)번호", schema = @Schema(implementation = Integer.class) )
    })
    public Map<String, Object>  tearcherLessionPopList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException
    {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            CommonUtil.setPageParam(mapReq); // Paging 값 세팅

            mapRlt.put("list" , dbSvc.dbList("teacher.tearcherLessionList" , mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 강사 메인에서 바로전 | 현재 | 이후 수업에 대한 조회를 한다.
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/teacherMainLessonList")
    @Operation(summary = "메뉴 : 강사 메인"
              , description = "강사 메인에서 바로전 | 현재 | 이후 수업에 대한 조회를 한다."
                            + "<br/> ING_NUM의 값이 0: 바로전 수업, 1:현재수업, 2:미래의 수업"
                            + "<br/> LEC_NO : 강좌번호"
                            + "<br/> LES_NO : 강의 (lesson) 번호"
                            + "<br/> LES_ROUND : 진행회차"
                            + "<br/> LES_DT : 강의시작일"
                            + "<br/> LES_FR_TM : 강의 시작시간"
                            + "<br/> LES_TO_TM : 강의 종료시간"
                            + "<br/> LES_DSC : 강의설명"
                            + "<br/> WEEK_NM : 요일"
                            + "<br/> SBJ_NM : 강좌명"
                            + "<br/> ING_NM : 진행상태"
                            + "<br/> MAIN_IMG : 이미지"
                            + "<br/> LEC_NM : 강좌명"
                            + "<br/> LEC_DSC : 강좌 설명"
                            + "<br/> LEC_INWON : 인원"
                            + "<br/> ROOM_ID : ROOM_ID"
    )
    @Parameters({
            @Parameter(name = "teacher_no", required = false, description = "강사번호", schema = @Schema(implementation = Integer.class) )
    })



    public Map<String, Object>  teacherMainLessonList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException
    {
        Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{

            mapRlt.put("list" , dbSvc.dbList("teacher.teacherMainLessonList" , mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

}
