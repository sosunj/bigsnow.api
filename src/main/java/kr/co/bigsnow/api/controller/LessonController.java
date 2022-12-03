package kr.co.bigsnow.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.bigsnow.api.service.CommonService;
import kr.co.bigsnow.api.util.CtrlUtil;
import kr.co.bigsnow.core.controller.StandardController;
import kr.co.bigsnow.core.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/lesson", produces = MediaType.APPLICATION_JSON_VALUE)
@SuppressWarnings({ "unchecked", "rawtypes" })
public class LessonController extends StandardController {

    /**
     * TransactionManager
     */
    @Autowired
    protected PlatformTransactionManager txManager;

    @Autowired
    protected CommonService commonService;


    /**
     * 홈 화면
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("/lessonSchedule")
    @Operation(summary = "메뉴 : 강의실 메인", description = "강의실 메인 페이지의 시간별 정보를 조회한다. 시스템 관리자가 아닌 강사가 로그인을 하게 되면 해당 강사의 정보만 조회합니다.")
    @Parameters({
            @Parameter(name = "lesson_dt", required = true, description = "일자(년월일)", schema = @Schema(implementation = String.class)),
            @Parameter(name = "teacher_no", required = false, description = "강사번호", schema = @Schema(implementation = Integer.class))
    })


    Map<String, Object> lessonSchedule(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
        Map<String, Object> mapReq	    = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt      = new HashMap();

        Exception rstEx                 = null;

        int nNum                        = 0;

        try{
            // mapReq.put("user_grp_cd" , CommonUtil.nvlMap(mapReq, "token_user_grp_cd")   );
            mapReq.put("cust_no"     , CommonUtil.nvlMap(mapReq, "token_cust_no")   );

            if ( "".equals( CommonUtil.nvlMap(mapReq, "lesson_dt") )) {
                mapReq.put("lesson_dt" , DateUtil.getCurrentDate());
            }

            mapReq.put("lesson_dt"      , CommonUtil.removeDateFormat(mapReq, "lesson_dt"));

            mapRlt.put("lessonList"     ,  dbSvc.dbList  ("lesson.mainLessonScheduleList" , mapReq)); // 수업 스케쥴

        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }



    /**
     * 홈 화면
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("/lessonScheduleList")
    @Operation(summary = "메뉴 : 강의실 메인", description = "스케쥴에 해당하는 학습정보를 표시한다.")
    @Parameters({
            @Parameter(name = "lesson_dt" , required = true, description = "일자(년월일)"   , schema = @Schema(implementation = String.class)),
            @Parameter(name = "teacher_no", required = false, description = "강사번호"      , schema = @Schema(implementation = Integer.class)),
            @Parameter(name = "hh"        , required = true , description = "시간(두자리)"  , schema = @Schema(implementation = String.class)),
            @Parameter(name = "mm"        , required = true , description = "분(두자리)"    , schema = @Schema(implementation = String.class)),
    })


    Map<String, Object> lessonScheduleList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
        Map<String, Object> mapReq	    = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt      = new HashMap();

        Exception rstEx                 = null;


        try{
            // mapReq.put("user_grp_cd" , CommonUtil.nvlMap(mapReq, "token_user_grp_cd")   );
            mapReq.put("cust_no"     , CommonUtil.nvlMap(mapReq, "token_cust_no")   );

            if ( "".equals( CommonUtil.nvlMap(mapReq, "lesson_dt") )) {
                mapReq.put("lesson_dt" , DateUtil.getCurrentDate());
            }

            mapReq.put("lesson_dt"      , CommonUtil.removeDateFormat(mapReq, "lesson_dt"));

            mapRlt.put("lessonList"     ,  dbSvc.dbList  ("lesson.lessonScheduleTimeList" , mapReq)); // 수업 스케쥴

        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }


    /**
     * 화상 교육에 대한 출석을 등록
     * 
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/insertAttendance")
    @Operation(summary = "메뉴: 홈 > 화상 출석 등록 ", description = "화상 교육에 대한 출석을 등록한다.")
    @Parameters({
            @Parameter(name = "lec_app_no", required = true, description = "수강신청 번호", schema = @Schema(implementation = Integer.class)),
            @Parameter(name = "les_no", required = true, description = "수강번호", schema = @Schema(implementation = Integer.class)) })
    public Map<String, Object> insertAttendance(HttpServletRequest request, HttpServletResponse response,
            RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        int rtnInt = 0;

        Exception rstEx = null;
        try {
            if (!"".equals(CommonUtil.nvlMap(mapReq, "lec_app_no"))) {

                int nCnt = dbSvc.dbCount("lesson.userAttendanceExistCount", mapReq);

                if (nCnt == 0) {
                    mapReq.put("att_yn", "Y");
                    dbSvc.dbInsert("lesson.insertAttendance", mapReq);
                }

                dbSvc.dbUpdate("lesson.updateLectureAppAtt", mapReq);
            }
        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 로그인한 수강생의 화상채팅방의 목록을 조회
     * 
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/myLessionVideoList")
    @Operation(summary = "메뉴: 홈/강의실/강의실 목록", description = "로그인한 수강생의 화상채팅방의 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "std_user_no", required = false, description = "로그인한 수강생 번호", schema = @Schema(implementation = Integer.class)),
            @Parameter(name = "teacher_no" , required = false, description = "로그인한 강사 번호", schema = @Schema(implementation = Integer.class)),
            @Parameter(name = "user_grp_cd", required = false, description = "로그인한 회원구분코드", schema = @Schema(implementation = String.class)),
            @Parameter(name = "cust_no"    , required = false, description = "로그인한 기관번호", schema = @Schema(implementation = String.class)),
            @Parameter(name = "lec_no"     , required = false, description = "강좌번호", schema = @Schema(implementation = String.class)),
            @Parameter(name = "page_now"   , required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class)),
            @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class)) })

    Map<String, Object> myLessionVideoList(HttpServletRequest request, HttpServletResponse response,        RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException
    {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        Exception rstEx = null;
        try {
            mapReq.put("cust_no", CommonUtil.nvlMap(mapReq, "token_cust_no"));

            // mapReq = getJwtTokenValue ( request, mapReq );

            CommonUtil.setPageParam(mapReq); // Paging 값 세팅

            mapRlt.put("count", dbSvc.dbInt("lesson.myLessionVideoCount", mapReq));
            mapRlt.put("list", dbSvc.dbList("lesson.myLessionVideoList", mapReq));

        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 로그인한 수강생의 수업 목록을 조회
     * 
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/myLessionList")
    @Operation(summary = "메뉴: 홈/강의실/강의실 목록", description = "로그인한 수강생의 수업 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "std_user_no", required = false, description = "로그인한 수강생 번호", schema = @Schema(implementation = Integer.class)),
            @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class)),
            @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class)) })
    Map<String, Object> myLessionList(HttpServletRequest request, HttpServletResponse response,
            RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        Exception rstEx = null;
        try {
            mapReq.put("cust_no", CommonUtil.nvlMap(mapReq, "token_cust_no"));

            // mapReq = getJwtTokenValue ( request, mapReq );

            CommonUtil.setPageParam(mapReq); // Paging 값 세팅

            mapRlt.put("list", dbSvc.dbList("lesson.myLessionList", mapReq));
            mapRlt.put("count", dbSvc.dbInt("lesson.myLessionCount", mapReq));
        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 로그인한 수강생의 수업 목록을 조회
     * 
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/myAttendList")
    @Operation(summary = "메뉴: 홈/강의실/강의실 목록", description = "로그인한 수강생의 수업 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "std_user_no", required = false, description = "로그인한 수강생 번호", schema = @Schema(implementation = Integer.class)) })
    public Map<String, Object> myAttendList(HttpServletRequest request, HttpServletResponse response,
            RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        Exception rstEx = null;
        try {
            // Paging 값 세팅
            CommonUtil.setPageParam(mapReq);
            mapReq.put("cust_no", CommonUtil.nvlMap(mapReq, "token_cust_no"));

            int pageRowCount = (Integer) mapReq.get("page_row_count");

            // mapReq = getJwtTokenValue ( request, mapReq );

            List lstRs = dbSvc.dbList("lesson.myAttendList", mapReq);
            int cntRs = dbSvc.dbCount("lesson.myAttendCount", mapReq);
            mapRlt.put("list", lstRs);
            mapRlt.put("count", cntRs);
            mapRlt.put("pageCount", cntRs / pageRowCount + (cntRs % pageRowCount > 0 ? 1 : 0));
        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 수업 목록을 조회
     * 
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/lessonList")
    @Operation(summary = "메뉴:수업관리 > 수업",
              description = "수업 목록을 조회한다." +
                      "<br/><br/>List 반환 결과" +
                      "<br/> - lec_no:강좌번호" +
                      "<br/> - les_no:수강번호" +
                      "<br/> - les_round:회차" +
                      "<br/> - les_dt:수강일" +
                      "<br/> - week_nm:요일" +
                      "<br/> - les_fr_tm:수강시작시간" +
                      "<br/> - les_to_tm:수강종료시간" +
                      "<br/> - att_num:출석인원" +
                      "<br/> - absent_num:결석인원" +
                      "<br/> - les_dsc:수업내용" +
                      "<br/> - lec_inwon:수강인원수" +
                      "<br/> - lec_nm:강좌명" +
                      "<br/> - inwon_rate:출석율" +
                      "<br/> - ing_nm:진행상태" +
                      "<br/> - lec_nm:강의명" +
                      "<br/> - cust_nm:기관명" +
                      "<br/> - teacher_nm:강사명"

    )
    @Parameters({
            @Parameter(name = "cust_nm", required = false, description = "기관명", schema = @Schema(implementation = String.class)),
            @Parameter(name = "cust_no", required = false, description = "기관번호", schema = @Schema(implementation = String.class)),

            @Parameter(name = "lec_nm", required = false, description = "강좌명", schema = @Schema(implementation = String.class)),
            @Parameter(name = "lec_year", required = false, description = "강의년도", schema = @Schema(implementation = String.class)),
            @Parameter(name = "lec_no", required = false, description = "강좌번호", schema = @Schema(implementation = String.class)),
            @Parameter(name = "teacher_no", required = false, description = "강사번호", schema = @Schema(implementation = String.class)),
            @Parameter(name = "prog_state_cd", required = false, description = "상태[01:종료,02:진행전]", schema = @Schema(implementation = String.class)),

            @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class)),
            @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))
     })
        Map<String, Object> lessonList(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException
    {

            Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
            Map<String, Object> mapRlt = new HashMap();

            Exception rstEx = null;
            try {
                mapReq.put("user_grp_cd"   , CommonUtil.nvlMap(mapReq, "token_user_grp_cd"));
                mapReq.put("token_cust_no" , CommonUtil.nvlMap(mapReq, "token_cust_no"));
                CommonUtil.setPageParam(mapReq); // Paging 값 세팅

                mapRlt.put("list", dbSvc.dbList("lesson.lessonList", mapReq));
                mapRlt.put("count", dbSvc.dbInt("lesson.lessonCount", mapReq));
            } catch (Exception ex) {
                rstEx = ex;
            }

            return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 수업 상세 정보 조회
     * 
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/lessonDetailAndList")
    @Operation(summary = "메뉴: 수업관리 > 수업 > 수업상세 리스트[강좌 상세]", description = "수업상세의 상단에 있는 상세정보를 조회한다.")
    @Parameters({
            @Parameter(name = "lec_no", required = true, description = "강좌 번호", schema = @Schema(implementation = String.class)),
            @Parameter(name = "prog_state_cd", required = false, description = "진행상태", schema = @Schema(implementation = String.class)),
            @Parameter(name = "les_no"  , required = false, description = "수업번호", schema = @Schema(implementation = Integer.class)),
            @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class)),
            @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))
            })

    Map<String, Object> lessonDetailAndList(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException
    {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        Exception rstEx = null;
        try {
            mapRlt.put("detail", dbSvc.dbDetail("lesson.lessonDetail", mapReq));

            CommonUtil.setPageParam(mapReq); // Paging 값 세팅

            mapRlt.put("list", dbSvc.dbList("lesson.lessonDetailAndList", mapReq));
            mapRlt.put("count", dbSvc.dbInt("lesson.lessonDetailAndCount", mapReq));
        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }



    /**
     * 수업관리
     *
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("/lessonAttendUserList")
    @Operation(summary = "메뉴: 수업관리 > 수업 > 수업관리 팝업", description = "나의 수업관리의 상세 팝업으로 수업내용 및 수강생의 출석체크를 한다.")
    @Parameters({
            @Parameter(name = "les_no"  , required = false, description = "수업번호", schema = @Schema(implementation = Integer.class))
    })

    Map<String, Object> lessonAttendUserList(HttpServletRequest request, HttpServletResponse response,
                                      RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        Exception rstEx = null;

        try {

            mapRlt.put("detail", dbSvc.dbDetail("lesson.lessonAttendDetail", mapReq));
            mapRlt.put("list"  , dbSvc.dbList  ("lesson.lessonAttendUserList", mapReq));

        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }


    /**
     * 수업관리 출석 저장
     *
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/lessonAttendUserUpdate")
    @Operation(summary = "메뉴: 수업관리 > 수업 > 수업관리 팝업", description = "나의 수업관리의 상세 팝업으로 수업내용 및 수강생의 출석체크를 한다.")
    @Parameters({

            @Parameter(name = "les_no"  , required = true , description = "수업번호"   , schema = @Schema(implementation = Integer.class))
         ,  @Parameter(name = "les_dsc" , required = false, description = "수업내용"   , schema = @Schema(implementation = String.class))
         ,  @Parameter(name = "att_no"  , required = false, description = "[배열]출석부 번호" , schema = @Schema(implementation = Integer.class))
         ,  @Parameter(name = "att_yn"  , required = false, description = "[배열]출석여부(Y/N)" , schema = @Schema(implementation = String.class))
         ,  @Parameter(name = "lec_app_no"  , required = false, description = "[배열]수강신청번호" , schema = @Schema(implementation = String.class))
    })

    Map<String, Object> lessonAttendUserUpdate(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException
    {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        Exception rstEx = null;

        try {

                dbSvc.dbUpdate("lesson.updateLessonDscUpdate", mapReq);

                String strSplit =  "`_`"; //String.valueOf ((char) 01);

                String[] arrLecAppNo = CommonUtil.nvlMap(mapReq, "lec_app_no").split(strSplit);

                String[] arrAttNo    = CommonUtil.nvlMap(mapReq, "att_no"   ).split(strSplit, arrLecAppNo.length);
                String[] arrAttYn    = CommonUtil.nvlMap(mapReq, "att_yn"   ).split(strSplit, arrLecAppNo.length);
                String[] arrFeedBack = CommonUtil.nvlMap(mapReq, "feed_back").split(strSplit, arrLecAppNo.length);

                if ( arrLecAppNo.length > 0 )
                {

                    for (int nIdx = 0; nIdx < arrLecAppNo.length; nIdx++)
                    {

                         mapReq.put("att_yn"    , arrAttYn[ nIdx ]);
                         mapReq.put("lec_app_no", arrLecAppNo[ nIdx ]);
                         mapReq.put("feed_back" , arrFeedBack[ nIdx ]);

                         if ( arrAttNo.length == 0 ||  arrAttNo[ nIdx ] == null || "".equals(arrAttNo[ nIdx ]) || "null".equals(arrAttNo[ nIdx ].toLowerCase()) ) {
                             dbSvc.dbInsert("lesson.insertAttendance"     , mapReq);
                         } else {
                             mapReq.put("att_no"    , arrAttNo[ nIdx ]);
                             dbSvc.dbUpdate("lesson.updateAttendanceAttYn", mapReq);
                         }

                        // 출석부에 출석여부의 건수를 적용함
                        dbSvc.dbUpdate("lesson.updateLectureAppAtt", mapReq);
                    }

                }

            // 출석부에 출석여부의 건수를 적용함
            dbSvc.dbUpdate("lesson.updateLessonAtt", mapReq);

        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }


    /**
     * 영상/자료 관리를 등록
     * 
     * @param request
     * @param response
     * @param requestFile
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping(path = "/lessonVideoDataFileInsert")
    @Operation(summary = "메뉴: 수업 관리 > 영상/자료 관리를 등록한다.", description = "영상자료를 insert하기 전에 기존 영상주소를 삭제한 후 신규로 insert합니다.")
    @Parameters({
            @Parameter(name = "les_no", required = true, description = "수강 번호", schema = @Schema(implementation = Integer.class)),
            @Parameter(name = "ttl", required = true, description = "영상제목", schema = @Schema(implementation = String[].class)),
            @Parameter(name = "video_url", required = true, description = "Video Url", schema = @Schema(implementation = String[].class)),
            @Parameter(name = "up_file", required = true, description = "첨부파일의 input tag를 [up_file]로 한다.") })

    public Map<String, Object> lessonVideoDataFileInsert(HttpServletRequest request, HttpServletResponse response,
            MultipartHttpServletRequest requestFile) throws JsonProcessingException {

        Map<String, Object> mapReq = CommonUtil.getRequestFileMap(request, "/upload/file/", false); // requestEntity
        Map<String, Object> mapRlt = new HashMap();

        Exception rstEx = null;
        try {
            FileManager fileMgr = new FileManager(request, dbSvc);

            String[] arrTtl = CommonUtil.getMapValArray(mapReq, "ttl");
            String[] arrVideoUrl = CommonUtil.getMapValArray(mapReq, "video_url");

            Map mapParam = new HashMap();
            mapParam.put("les_no", CommonUtil.nvlMap(mapReq, "les_no"));

            if (arrTtl != null && !"".equals(arrTtl[0])) {

                dbSvc.dbDelete("lesson.deleteAllVideoData", mapParam);

                for (int nLoop = 0; nLoop < arrTtl.length; nLoop++) {
                    mapParam.put("ttl", arrTtl[nLoop]);
                    mapParam.put("video_url", arrVideoUrl[nLoop]);

                    dbSvc.dbInsert("lesson.insertVideoData", mapParam);
                }
            }

            List fileList = (List) mapReq.get(CoreConst.MAP_UPFILE_KEY);

            if (fileList != null && !fileList.isEmpty()) {
                mapReq.put("ref_pk", CommonUtil.nvlMap(mapReq, "les_no")); // 테이블 키
                mapReq.put("ref_nm", "TB_VIDEODATA"); // 테이블 명

                fileMgr.fileDbSave(mapReq);
            }

            // 파일처리가 필요함

        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 영상/자료를 조회
     * 
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/lessonVideoList")
    @Operation(summary = "메뉴: 수업관리 > 수업 > 영상/자료 관리의 정보를 조회", description = "영상/자료를 조회한다.")
    @Parameters({
            @Parameter(name = "les_no", required = true, description = "수강 번호", schema = @Schema(implementation = Integer.class)) })
    Map<String, Object> lessonVideoList(HttpServletRequest request, HttpServletResponse response,
            RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        Exception rstEx = null;
        try {
            mapRlt.put("video", dbSvc.dbList("lesson.lessonVideoData", mapReq));
            mapRlt.put("file", dbSvc.dbList("lesson.lessonVideoFile", mapReq));
        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 출석부를 출력하기 위한 수강생 정보 조회
     * 
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/lessonAttendList")
    @Operation(summary = "메뉴: 수업관리 > 수업 > 출석부 팝업", description = "출석부를 출력하기 위한 수강생 정보를 표시한다.")
    @Parameters({
            @Parameter(name = "les_no", required = true, description = "수강 번호", schema = @Schema(implementation = Integer.class)) })
    Map<String, Object> lessonAttendList(HttpServletRequest request, HttpServletResponse response,
            RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        Exception rstEx = null;
        try {
            Map<String, Object> dtlMap = dbSvc.dbDetail("lesson.lessonAttendDetail", mapReq);
            mapRlt.put("detail", dtlMap);

            mapRlt.put("list"  , dbSvc.dbList  ("lesson.lessonAttendUserList", mapReq));
/*
            mapReq.put("lec_no", dtlMap.get("lec_no"));
            List lstRs = dbSvc.dbList("lesson.lectureappUserList", mapReq);

            // mapRlt.put("user_list" , lstRs);

            if (lstRs != null) {

                String strUserNo = "";

                for (int nLoop = 0; nLoop < lstRs.size(); nLoop++) {
                    Map mapRs = (Map) lstRs.get(nLoop);

                    if (!"".equals(strUserNo))
                        strUserNo += ",";

                    strUserNo += CommonUtil.nvlMap(mapRs, "std_user_no");
                }

                mapReq.put("arr_user_no", strUserNo.split(","));
                mapRlt.put("list", makeLessonAttendDateList(dbSvc.dbList("lesson.lessonAttendDateList", mapReq)));
            }
 */

        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     *
     * @param lstData
     * @return
     */
    private List makeLessonAttendDateList(List lstData) {

        if (lstData == null || lstData.isEmpty()) {
            return null;
        }

        try {

            for (int nLoop = 0; nLoop < lstData.size(); nLoop++) {
                Map mapRs = (Map) lstData.get(nLoop);

                int nInwon = CommonUtil.nvlMapInt(mapRs, "tot_inwon");

                List lstVal = new ArrayList();

                for (int nIdx = 0; nIdx < nInwon; nIdx++) {
                    Map mapVal = new HashMap();

                    mapVal.put("user_nm", CommonUtil.nvlMap(mapRs, "user_nm_" + nIdx));
                    mapVal.put("user_att", CommonUtil.nvlMap(mapRs, "user_att_" + nIdx));

                    mapRs.remove("user_nm_" + nIdx);
                    mapRs.remove("user_att_" + nIdx);

                    lstVal.add(mapVal);

                }

                mapRs.put("list", lstVal);
            }
        } catch (Exception e) {
        }
        return lstData;
    }

    /**
     * 수강생 목록 조회
     * 
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/lessonStudentList")
    @Operation(summary = "메뉴: 수업관리 > 수강생"
             , description = "수강생 목록을 조회한다.<br/>"
              + "List 반환 필드 = cust_nm:기관명 , lec_nm:강좌명, std_user_nm:수강생명, gender_gbn:성별, att_num:출석건수, absent_num:결석건수" +
            ", tot_round:전체수업, inwon_rate: 출석률, tarchar_nm:강사명, reg_dt:등록일, feedback_num:피드백 건수, lec_app_no:수강신청번호" +
            ", lec_no:강좌번호, , user_no:학생등록번호 , hp_no:핸드폰번호, user_id:학생회원번호, ing_nm:진행상태, age:연령, prog_round:진행수업(종료수업)"
    )
    @Parameters({
            @Parameter(name = "cust_no", required = false, description = "기관 번호", schema = @Schema(implementation = Integer.class)),
            @Parameter(name = "cust_nm", required = false, description = "기관 명", schema = @Schema(implementation = String.class)),
            @Parameter(name = "lec_no", required = false, description = "강좌 번호", schema = @Schema(implementation = String.class)),
            @Parameter(name = "lec_nm", required = false, description = "강좌 명", schema = @Schema(implementation = String.class)),
            @Parameter(name = "lec_year", required = false, description = "강좌 년도", schema = @Schema(implementation = String.class)),

            @Parameter(name = "teacher_no", required = false, description = "강사번호", schema = @Schema(implementation = Integer.class)),
            @Parameter(name = "std_user_nm", required = false, description = "수강생명", schema = @Schema(implementation = String.class)) })

    Map<String, Object> lessonStudentList(HttpServletRequest request, HttpServletResponse response,
            RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        Exception rstEx = null;
        try {
            mapReq.put("user_grp_cd", CommonUtil.nvlMap(mapReq, "token_user_grp_cd"));
            mapReq.put("token_cust_no", CommonUtil.nvlMap(mapReq, "token_cust_no"));
            CommonUtil.setPageParam(mapReq); // Paging 값 세팅

            mapRlt.put("list" , dbSvc.dbList("lesson.lessonStudentList", mapReq));
            mapRlt.put("count", dbSvc.dbCount("lesson.lessonStudentCount", mapReq));
        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 수강생 목록에서 피드백 팝업의 목록을 조회
     * 
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/lessonStudentFeedbackList")
    @Operation(summary = "메뉴: 수업관리 > 수강생 : 피드백 팝업", description = "수강생 목록에서 피드백 팝업의 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "lec_app_no", required = false, description = "수강신청 번호", schema = @Schema(implementation = Integer.class)) })
    Map<String, Object> lessonStudentFeedbackList(HttpServletRequest request, HttpServletResponse response,
            RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        Exception rstEx = null;
        try {
            List lstRs = dbSvc.dbList("lesson.lessonStudentFeedbackList", mapReq);

            mapRlt.put("list", lstRs);

            if (lstRs == null) {
                mapRlt.put("count", "0");
            } else {
                mapRlt.put("count", lstRs.size());
            }
        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 피드백을 저장
     * 
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/insertFeedback")
    @Operation(summary = "메뉴: 수업관리 > 수강생 : 피드백 팝업(피드백 등록) ", description = "피드백을 저장한다.")
    @Parameters({
            @Parameter(name = "lec_app_no", required = true, description = "수강신청 번호", schema = @Schema(implementation = Integer.class)),
            @Parameter(name = "ctnt", required = true, description = "피드백 내용", schema = @Schema(implementation = String.class)) })
    public Map<String, Object> insertFeedback(HttpServletRequest request, HttpServletResponse response,
            RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        int rtnInt = 0;
        Exception rstEx = null;
        try {
            dbSvc.dbInsert("lesson.insertFeedback", mapReq);

            dbSvc.dbInsert("lesson.updateFeedbackCount", mapReq);
        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 피드백을 삭제
     * 
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/deleteFeedback")
    @Operation(summary = "메뉴: 수업관리 > 수강생 : 피드백 팝업(피드백 삭제) ", description = "피드백을 삭제한다.")
    @Parameters({
            @Parameter(name = "lec_app_no", required = true, description = "수강신청 번호", schema = @Schema(implementation = Integer.class)),
            @Parameter(name = "fb_no", required = true, description = "피드백 번호", schema = @Schema(implementation = Integer.class)) })
    public Map<String, Object> deleteFeedback(HttpServletRequest request, HttpServletResponse response,
            RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        int rtnInt = 0;
        Exception rstEx = null;
        try {
            dbSvc.dbInsert("lesson.deleteFeedback", mapReq);
            dbSvc.dbInsert("lesson.updateFeedbackCount", mapReq);
        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 수강생 저장
     * 
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/insertStudent")
    @Operation(summary = "메뉴: 수업관리 > 수강생 : 수강생 등록 팝업(수강생 등록) ", description = "수강생 저장한다.")
    @Parameters({
            @Parameter(name = "std_user_no", required = true, description = "수강생 번호(tb_user에서 조회한다.)", schema = @Schema(implementation = Integer.class)),
            @Parameter(name = "lec_no", required = true, description = "강좌번호", schema = @Schema(implementation = Integer.class)) })
    public Map<String, Object> insertStudent(HttpServletRequest request, HttpServletResponse response,
            RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        int rtnInt = 0;
        Exception rstEx = null;
        try {

                String[] arrStdUserNo = CommonUtil.nvlMap(mapReq, "std_user_no").split(",");

                if ( !"".equals(CommonUtil.nvlMap(mapReq, "std_user_no"))) {

                    for (int nIdx = 0; nIdx < arrStdUserNo.length; nIdx++) {

                        mapReq.put("std_user_no", arrStdUserNo[ nIdx ]);

                        List lstRs = dbSvc.dbList("lesson.lessonStudentDupChk", mapReq);

                        if (lstRs == null || lstRs.isEmpty()) {

                            dbSvc.dbInsert("lesson.insertLectureapp", mapReq);
                            dbSvc.dbUpdate("lecture.updateLectureInwonCount", mapReq);

                        } else {
                            // CtrlUtil.settingRst(mapRlt, 400, "이미 등록된 수강생입니다.", "N");
                        }

                    }
                    CtrlUtil.settingRst(mapRlt, 200, "정상적으로 저장되었습니다.", "Y");

                }

        } catch (Exception ex) {
            rstEx = ex;
            mapRlt = buildResult(mapRlt, mapReq, rstEx, request);
        }
        return mapRlt;
    }

    /**
     * 엑셀 업로드를 통해 수강생 저장
     * 
     * @param request
     * @param response
     * @param requestFile
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/insertStudentExcelUpload")
    @Operation(summary = "메뉴: 수업관리 > 수강생 : 수강생 등록 팝업(엑셀 등록) ", description = "엑셀 업로드를 통해 수강생 저장한다.")
    @Parameters({
            @Parameter(name = "up_file", required = true, description = "업로드 파일명", schema = @Schema(implementation = String.class)) })
    public Map<String, Object> insertStudentExcelUpload(HttpServletRequest request, HttpServletResponse response,
            MultipartHttpServletRequest requestFile) throws JsonProcessingException {

        Map<String, Object> mapReq = CommonUtil.getRequestFileMap(request, "/upload/excel/", false); // requestEntity
        Map<String, Object> mapRlt = new HashMap();

        Exception rstEx = null;
        try {
            // 기관명, 강좌번호, 강좌명, 수강생번호, 수강생명

            FileManager fileMgr = new FileManager(request, dbSvc);

            mapReq.put(CoreConst.INPUT_DUPCHECK, "Y");
            mapReq.put("excel_header_id", CoreConst.EXCEL_HD_LESSON_STUDENT);

            mapRlt = commonService.excelUploadProc(request, response, mapReq, fileMgr);

            insertStudentExcelUploadInwon(request, response, mapReq, fileMgr);
        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 강의의 인원수를 업데이트 한다.
     * 
     * @param request
     * @param response
     * @param mapReq
     * @param fileMgr
     * @return
     */
    // 강의의 인원수를 업데이트 한다.
    public Map insertStudentExcelUploadInwon(HttpServletRequest request, HttpServletResponse response, Map mapReq,
            FileManager fileMgr) {

        Map<String, Object> mapResult = new HashMap<String, Object>();

        ExcelHeader excelHeader = new ExcelHeader();

        String strFlag = CommonUtil.nvlMap(mapReq, "iflag");

        int nResult = 1;

        try {

            String strExcelHDId = CommonUtil.nvlMap(mapReq, "excel_header_id").toUpperCase();

            Map mapExcelInfo = excelHeader.getHeader(strExcelHDId);

            List lstFile = (List) mapReq.get(CoreConst.MAP_UPFILE_KEY);

            // StringBuffer sb = new StringBuffer(); // fileMgr.getMsgBuffer(); // 오류 메시지

            if (lstFile != null && !lstFile.isEmpty()) {

                for (int nLoop = 0; nLoop < lstFile.size(); nLoop++) {

                    Map mapFile = (Map) lstFile.get(nLoop);
                    List lstRs = fileMgr.excelFileToList(CommonUtil.nvlMap(mapFile, "phy_file_nm"), mapExcelInfo);

                    if (lstRs != null && !lstRs.isEmpty()) {

                        for (int nIdx = 0; nIdx < lstRs.size(); nIdx++) {
                            Map mapRs = (Map) lstRs.get(nIdx);
                            dbSvc.dbUpdate("lecture.updateLectureInwonCount", mapRs);
                        }
                    }
                }
            }

        } catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {
            super.errorLogWrite(request, mapReq, e1);

            nResult = -100;

        } catch (DataAccessException e2) {
            super.errorLogWrite(request, mapReq, e2);
            nResult = -101;

        } catch (Exception e) {
            super.errorLogWrite(request, mapReq, e);
            nResult = -102;
        }

        if (nResult < 0) {
            mapResult.put("msg", "데이터를 저장하는데 실패하였습니다. 관리자에게 문의하시길 바랍니다.");
            mapResult.put("status", "N");

        } else {
            mapResult.put("status", "Y");
        }

        return mapResult;
    }

    /**
     * 수강생 상세정보를 조회
     * 
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/lessonStudentDetail")
    @Operation(summary = "메뉴: 수업관리 > 수강생 : 수강생 상세",
               description = "수강생 상세정보를 조회한다." +
                       "<br/><br/>list 반환 필드" +
                       "<br/> - les_dt:수업일자" +
                       "<br/> - les_fr_tm:시작시간" +
                       "<br/> - les_to_tm:종료시간" +
                       "<br/> - att_yn:출석여부" +
                       "<br/> - les_dsc:수업내용" +
                       "<br/> - week_nm:요일명" +
                       "<br/> - att_num:출석인원" +
                       "<br/> - absent_num:결석인원" +
                       "<br/> - reg_dt:등록일" +
                       "<br/> - les_round:회차" +
                       "<br/> - ing_nm:상태"
    )

    @Parameters({
            @Parameter(name = "lec_app_no", required = true, description = "수강신청번호", schema = @Schema(implementation = Integer.class)),
            @Parameter(name = "lec_no", required = true, description = "강좌번호", schema = @Schema(implementation = Integer.class)) ,
            @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class)),
            @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))
       })


        Map<String, Object> lessonStudentDetail(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        Exception rstEx = null;
        try {
            mapRlt.put("detail", dbSvc.dbDetail("lesson.lessonLectureAppDetail", mapReq));

            int nCnt = dbSvc.dbCount("lesson.lessonAttendanceDetailCount", mapReq);
            if ( nCnt > 0 ) {
                CommonUtil.setPageParam(mapReq); // Paging 값 세팅
                mapRlt.put("list", dbSvc.dbList("lesson.lessonAttendanceDetailList", mapReq));
            }

            mapRlt.put("count", nCnt);

        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 기관에 해당하는 강좌와 수강생 목록
     * 
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("/lectureStudentList")
    @Operation(summary = "메뉴 >수업관리 > 수강생 등록 팝업", description = "기관에 해당하는 강좌와 수강생 목록을 조회")
    @Parameters({
            @Parameter(name = "cust_no", required = true, description = "선택한 기관번호", schema = @Schema(implementation = String.class)) })
    Map<String, Object> lectureStudentList(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException
    {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        ObjectMapper mapper = new ObjectMapper();

        List<Map<String, Object>> lstRs = null;
        Exception rstEx = null;
        try {
            mapRlt.put("lecture", dbSvc.dbList("lecture.lectureList", mapReq)); // 강좌 목록

            mapReq.put("user_gbn_cd", "UGP001"); // 수강생
            mapRlt.put("student", dbSvc.dbList("user.userList", mapReq));
        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 수강생 삭제
     * 
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/lectureappDelete")
    @Operation(summary = "메뉴 : 수강생을 삭제한다.", description = "수강생 삭제한다.(use_yn 값을 N으로 세팅한다.) ")
    @Parameters({
            @Parameter(name = "lec_app_no", required = true, description = "수강 등록번호", schema = @Schema(implementation = Integer.class)) })
    public Map<String, Object> lectureappDelete(HttpServletRequest request, HttpServletResponse response,
            RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        int deleteRow = 0;
        Exception rstEx = null;
        try {
            if (!"".equals(CommonUtil.nvlMap(mapReq, "lec_app_no"))) {

                Map mapRs = dbSvc.dbDetail("lesson.lessonLectureAppDetail", mapReq );

                deleteRow = dbSvc.dbUpdate("lesson.updateLectureAppUseYn", mapReq);

                mapReq.put("lec_no" , CommonUtil.nvlMap(mapRs, "lec_no"));

                dbSvc.dbUpdate("lecture.updateLectureInwonCount", mapReq ); // 인원수 재 조정

                CtrlUtil.settingRst(mapRlt, 200, "success", "Y");

            } else {
                CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
            }
        } catch (Exception ex) {
            rstEx = ex;
            mapRlt = buildResult(mapRlt, mapReq, rstEx, request);
        }
        return mapRlt;
    }

    /**
     * 수업에 해당하는 수강생 목록
     * 
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("/lessonAttendDateUserList")
    @Operation(summary = "강사메인 >회원정보 ", description = "수업에 해당하는 수강생 목록을 조회")
    @Parameters({
            @Parameter(name = "les_no", required = true, description = "선택한 수업번호", schema = @Schema(implementation = String.class)) })
    Map<String, Object> lessonAttendDateUserList(HttpServletRequest request, HttpServletResponse response,
            RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        Exception rstEx = null;
        try {
            mapRlt.put("list", dbSvc.dbList("lesson.lessonAttendDateUserList", mapReq)); // 수업별 회원정보
        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 모바일에서 수강생이 학습할 수 있도록 데이터 표시
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("/stdUserLessonInfo")
    @Operation(summary = "메뉴 : 수강생 학습"
            , description = "수강생이 모바일로 앞으로 진행될 본인의 수업을 할 수 있도록 한다."
            + "<br/> LEC_NO : 강좌번호"
            + "<br/> LES_NO : 강의 (lesson) 번호"
            + "<br/> LES_ROUND : 진행회차"
            + "<br/> LES_DT : 강의시작일"
            + "<br/> LES_FR_TM : 강의 시작시간"
            + "<br/> LES_TO_TM : 강의 종료시간"
            + "<br/> WEEK_NM : 요일"
            + "<br/> LEC_NM : 강좌명"
            + "<br/> ROOM_ID : ROOM_ID"
            + "<br/> RMND_SECTM : 학습을 시작하기전 초단위 시간"
    )
    @Parameters({
            @Parameter(name = "std_user_no", required = false, description = "수강생 회원번호", schema = @Schema(implementation = Integer.class) )
    })

    public Map<String, Object>  stdUserLessonInfo(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException
    {
        Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{

            mapRlt.put("list" , dbSvc.dbDetail("lesson.stdUserLessonInfo" , mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }


}
