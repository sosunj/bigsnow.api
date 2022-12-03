package kr.co.bigsnow.api.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.bigsnow.api.jwt.JwtUserDetailsService;
import kr.co.bigsnow.api.service.CommonService;
import kr.co.bigsnow.api.util.CtrlUtil;
import kr.co.bigsnow.core.controller.StandardController;
import kr.co.bigsnow.core.util.CommonUtil;
import kr.co.bigsnow.core.util.CoreConst;
import kr.co.bigsnow.core.util.FileManager;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@RestController
@RequestMapping(path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
// @RequestMapping(path = "/user" )
@SuppressWarnings({"unchecked", "rawtypes"})
public class UserController extends StandardController {

	/**
	 * TransactionManager
	 */
	@Autowired
	protected PlatformTransactionManager txManager;


	@Autowired
	protected CommonService commonService;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	// 토큰 유효기간
	//private int tokenExpireDay = 90;
    private int tokenExpireDay = 9000;


	/**
	 * 회원 상세정보 조회
	 *
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
    @GetMapping("/userDetail")
    @Operation(summary = "메뉴 : 회원관리 > 사용자 : 회원 및 강사 상세", description = "강사 및 수강생은 같은 테이블을 사용하고 있으며 user_gbn_cd로 구분한다.")
	@Parameters({
		@Parameter(name = "user_no", required = true, description = "회원 번호", schema = @Schema(implementation = Integer.class))
	})
    public Map<String, Object> userDetail(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt  = new HashMap();

        Exception rstEx             = null;
        try{
            mapRlt.put("detail" , dbSvc.dbDetail("user.userDetail" , mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }


    /**
     * 로그인 처리
     *
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/loginTest")
    @Operation(summary = "화면 : 로그인 화면", description = "로그인 정보를 조회한다.")
    @Parameters({
            @Parameter(name = "user_id", required = true, description = "회원ID", schema = @Schema(implementation = String.class)),
            @Parameter(name = "user_passwd", required = true, description = "비밀번호", schema = @Schema(implementation = String.class))
    })
    public Map<String, Object> loginTest(HttpServletRequest request, HttpServletResponse response,
                                     RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        Exception rstEx = null;
        try {
            Map<String, Object> userInfo = dbSvc.dbDetail("user.login", mapReq);

            String authYn = "N";
            String msg = "조회된 회원이 없습니다.";
            Object data = userInfo;

            if (userInfo != null) {
                // token 생성
                final String token = userDetailsService.createTokenExpireDay(
                        CommonUtil.nvlMap(userInfo, "user_no"), CommonUtil.nvlMap(userInfo, "user_nm"),
                        CommonUtil.nvlMap(userInfo, "user_id"), CommonUtil.nvlMap(userInfo, "cust_no"),
                        CommonUtil.nvlMap(userInfo, "user_grp_cd"), tokenExpireDay);

                authYn = "Y";
                msg = "회원정보를 조회하였습니다.";

                log.debug("====>token:" + token);

                mapRlt.put("token", token);
                mapRlt.put("ip_addr", CommonUtil.getClientIP(request));
                dbSvc.dbInsert("user.insertUserHist", userInfo);
            }

            mapRlt.put("data", data);
            mapRlt.put("authYn", authYn);
            mapRlt.put("msg", msg);

            log.debug("---------------------------------------------------------------------------------------");
            log.debug("LOGIN INFO:" + mapRlt.toString());
            log.debug("---------------------------------------------------------------------------------------");

        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 로그인 처리
     *
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/login")
    @Operation(summary = "화면 : 로그인 화면", description = "로그인 정보를 조회한다.")
    @Parameters({
            @Parameter(name = "user_id", required = true, description = "회원ID", schema = @Schema(implementation = String.class)),
            @Parameter(name = "user_passwd", required = true, description = "비밀번호", schema = @Schema(implementation = String.class))
    })
    public Map<String, Object> login(HttpServletRequest request, HttpServletResponse response,
            RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        Exception rstEx = null;
        try {
            Map<String, Object> userInfo = dbSvc.dbDetail("user.login", mapReq);

            String authYn = "N";
            String msg = "조회된 회원이 없습니다.";
            Object data = userInfo;

            if (userInfo != null) {
                // token 생성
                final String token = userDetailsService.createTokenExpireDay(
                        CommonUtil.nvlMap(userInfo, "user_no"), CommonUtil.nvlMap(userInfo, "user_nm"),
                        CommonUtil.nvlMap(userInfo, "user_id"), CommonUtil.nvlMap(userInfo, "cust_no"),
                        CommonUtil.nvlMap(userInfo, "user_grp_cd"), tokenExpireDay);

                authYn = "Y";
                msg = "회원정보를 조회하였습니다.";

                mapRlt.put("token", token);
                mapRlt.put("ip_addr", CommonUtil.getClientIP(request));
                dbSvc.dbInsert("user.insertUserHist", userInfo);
            }

            mapRlt.put("data", data);
            mapRlt.put("authYn", authYn);
            mapRlt.put("msg", msg);

            log.debug("---------------------------------------------------------------------------------------");
            log.debug("LOGIN INFO:" + mapRlt.toString());
            log.debug("---------------------------------------------------------------------------------------");

        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * Authorization Token을 통해 로그인 인증 처리
     *
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/userAuthorization")
    @Operation(summary = "로그인 인증 처리", description = "token을 통해 자동 로그인 처리")
    public Map<String, Object> userAuthorization(HttpServletRequest request, HttpServletResponse response,
            RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap<String, Object>();

        Exception rstEx = null;
        try {
            // token 확인
            Map<String, Object> userInfo = null;
            final String headerToken = (String) mapReq.get("token");
            if (!StringUtils.isAllEmpty(headerToken)) {
                // token 유효성 검사
                Map<String, Object> claimMap = userDetailsService.verifyJWT(headerToken);

                // token이 유효할 때
                if (claimMap != null) {
                    Map<String, Object> qParam = new HashMap<>();
                    qParam.put("user_id", claimMap.get("user_id"));
                    // 사용자 정보 조회
                    userInfo = dbSvc.dbDetail("user.userIdDetail", qParam);
                }
            }

            String authYn = "N";
            String msg = "로그인 인증 실패하였습니다.";
            Object data = userInfo;

            // 인증 성공일 때
            if (userInfo != null) {
                // token 생성
                final String token = userDetailsService.createTokenExpireDay(
                        CommonUtil.nvlMap(userInfo, "user_no"), CommonUtil.nvlMap(userInfo, "user_nm"),
                        CommonUtil.nvlMap(userInfo, "user_id"), CommonUtil.nvlMap(userInfo, "cust_no"),
                        CommonUtil.nvlMap(userInfo, "user_grp_cd"), tokenExpireDay);

                msg = "로그인 인증 성공하였습니다.";
                authYn = "Y";

                mapRlt.put("token", token);
                mapRlt.put("ip_addr", CommonUtil.getClientIP(request));
                dbSvc.dbInsert("user.insertUserHist", userInfo);
            }

            mapRlt.put("data", data);
            mapRlt.put("authYn", authYn);
            mapRlt.put("msg", msg);

            log.debug("---------------------------------------------------------------------------------------");
            log.debug("Authorization INFO:" + mapRlt.toString());
            log.debug("---------------------------------------------------------------------------------------");

        } catch (Exception ex) {
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }


    /**
     * 수강생 찾기를 위한 팝업화면
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/studentPopList")
    @Operation(summary = "메뉴 : 수강생 등록 > 수강생 찾기를 위한 팝업화면", description = "수강생 목록를 조회합니다.")
    @Parameters({
    		@Parameter(name = "user_nm"   , required = false, description = "수강생명", schema = @Schema(implementation = String.class) )
    	,   @Parameter(name = "hp_no"     , required = false, description = "핸드폰 번호", schema = @Schema(implementation = String.class) )
    })
    public Map<String, Object>  studentPopList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity  )  throws JsonProcessingException {
   	    Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt = new HashMap();

        Exception rstEx                 = null;
        try{
            mapReq.put("user_gbn_cd", "UGP001"); // 수강생 코드

            if (!CommonUtil.isAdmin(CommonUtil.nvlMap(mapReq, "token_user_grp_cd"))) {
                mapReq.put("cust_no" , CommonUtil.nvlMap(mapReq, "token_cust_no")   );
            }

            List lstRs = dbSvc.dbList("user.teacherPopList" , mapReq);

            mapRlt.put("list", lstRs );

            if ( lstRs != null ) {
               mapRlt.put("count", lstRs.size() );
            }
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }


    /**
     * 강사찾기 강사 검색을 위한 팝업화면
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/teacherPopList")
    @Operation(summary = "메뉴 : 강좌 관리 > 강사찾기 강사 검색을 위한 팝업화면", description = "강사 목록를 조회합니다.")
    @Parameters({
    		@Parameter(name = "user_nm"   , required = false, description = "강사명", schema = @Schema(implementation = String.class) )
    	,	@Parameter(name = "sbj_grp_nm", required = false, description = "과목그룹명", schema = @Schema(implementation = String.class) )
    	,   @Parameter(name = "hp_no"     , required = false, description = "핸드폰 번호", schema = @Schema(implementation = String.class) )
    })
    public Map<String, Object>  teacherPopList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity  )  throws JsonProcessingException{
   	    Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt  = new HashMap();

        Exception rstEx             = null;
        try{
            mapReq.put("user_gbn_cd", "UGP002"); // 강사코드

            if (!CommonUtil.isAdmin(CommonUtil.nvlMap(mapReq, "token_user_grp_cd"))) {
                mapReq.put("cust_no" , CommonUtil.nvlMap(mapReq, "token_cust_no")   );
            }

            List lstRs = dbSvc.dbList("user.teacherPopList" , mapReq);
            mapRlt.put("list", lstRs );

            if ( lstRs != null ) {
               mapRlt.put("count", lstRs.size() );
            }
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 강사 목록을 조회
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/teacherList")
    @Operation(summary = "강사 목록", description = "강사 목록을 조회합니다.")
    @Parameters({
    		@Parameter(name = "user_nm", required = false, description = "강사명", schema = @Schema(implementation = String.class) )
    	,   @Parameter(name = "hp_no", required = false, description = "핸드폰 번호", schema = @Schema(implementation = String.class) )
    	,   @Parameter(name = "cust_no", required = false, description = "기관번호", schema = @Schema(implementation = Integer.class) )
    	,   @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class))
    	,   @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))
    })
    public Map<String, Object>  teacherList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity  )  throws JsonProcessingException{
        Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt = new HashMap();

        Exception rstEx                 = null;
        try{
            mapReq.put("user_gbn_cd", "UGP001"); // 수강생

            CommonUtil.setPageParam(mapReq);

            mapRlt.put("list" , dbSvc.dbList("user.userList" , mapReq));
            mapRlt.put("count", dbSvc.dbCount("user.userCount" , mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 강사정보를 엑셀 업로드
     *
     * @param request
     * @param response
     * @param requestFile
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping( value="/teacherExcelUpload") /* , consumes = { MediaType.MULTIPART_FORM_DATA_VALUE } */
    @Operation(summary = "메뉴 : 강사관리 > 강사 >강사 엑셀 업로드", description = "강사정보를 엑셀 업로를 통해 등록한다")
    @Parameters({
    	   @Parameter(name = "cust_no", required = true, description = "기관번호", schema = @Schema(implementation = Integer.class) )
    	 , @Parameter(name = "up_file", required = true,  description = "엑셀 첨부 파일", schema = @Schema(implementation = Integer.class)  )

    })
    public Map<String, Object>  teacherExcelUpload (HttpServletRequest  request, HttpServletResponse response , MultipartHttpServletRequest requestFile )  throws JsonProcessingException{
        Map<String, Object> mapReq 	= CommonUtil.getRequestFileMap(request, "/upload/excel/", false); // requestEntity
    	Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            FileManager fileMgr = new FileManager(request, dbSvc);
            mapReq.put("user_grp_cd", "UGP002");  // 회원구분코드

            mapReq.put(CoreConst.INPUT_DUPCHECK, "Y");
            mapReq.put("excel_header_id", CoreConst.EXCEL_HD_TEACHER );

            mapRlt = commonService.excelUploadProc( request,  response, mapReq, fileMgr);
        }catch (Exception ex){
            rstEx = ex;
        }

        if(rstEx != null) {
            mapRlt = buildResult(mapRlt, mapReq, rstEx, request);
        }
        else {
            if ( !"Y".equals( CommonUtil.nvlMap(mapRlt, "status"))) {
                CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
            } else {
                CtrlUtil.settingRst(mapRlt, 200, "success", "Y");
            }
        }

        return mapRlt;
    }

    /**
     * 사용자[수강생] 목록
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/userList")
    @Operation(summary = "메뉴 : 회원관리 > 사용자[수강생] 목록", description = "회원 목록을 조회합니다.")
    @Parameters({
    		@Parameter(name = "user_nm", required = false, description = "강사명", schema = @Schema(implementation = String.class) )
    	,   @Parameter(name = "hp_no", required = false, description = "핸드폰 번호", schema = @Schema(implementation = String.class) )
    	,   @Parameter(name = "cust_no", required = false, description = "기관번호", schema = @Schema(implementation = Integer.class) )
    	,   @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class))
    	,   @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))

    })
    public Map<String, Object>  userList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity  )  throws JsonProcessingException
    {
        Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt = new HashMap();

        List<Map<String, Object>> lstRs = null;
        Exception rstEx                 = null;
        try{
            mapReq.put("user_gbn_cd", "UGP001"); // 수강생

            CommonUtil.setPageParam(mapReq);

            mapRlt.put("list" , dbSvc.dbList ("user.userList" , mapReq));
            mapRlt.put("count", dbSvc.dbCount("user.userCount" , mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }


    /**
     * 사용자[수강생] >회원 엑셀 업로드
     * @param request
     * @param response
     * @param requestFile
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping( value="/userExcelUpload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "메뉴 : 회원관리 > 사용자[수강생] >회원 엑셀 업로드", description = "회원정보를 엑셀 업로를 통해 등록한다")
    @Parameters({
    	   @Parameter(name = "cust_no", required = true, description = "기관번호", schema = @Schema(implementation = Integer.class) )
    	 , @Parameter(name = "up_file", required = true,  description = "엑셀 첨부 파일", schema = @Schema(implementation = Integer.class)  )
    })
    public Map<String, Object>  userExcelUpload (HttpServletRequest  request, HttpServletResponse response , MultipartHttpServletRequest requestFile )  throws JsonProcessingException
    {
        Map<String, Object> mapReq = CommonUtil.getRequestFileMap(request, "/upload/excel/", false);
    	Map<String, Object> mapRlt = new HashMap();

        Exception rstEx                 = null;
        try{
            FileManager fileMgr = new FileManager(request, dbSvc);
            mapReq.put("user_grp_cd", "UGP001");  // 회원구분코드(수강생)

            mapReq.put(CoreConst.INPUT_DUPCHECK, "Y");
            mapReq.put("excel_header_id", CoreConst.EXCEL_HD_STUDENT );

            mapRlt = commonService.excelUploadProc( request,  response, mapReq, fileMgr);
        }catch (Exception ex){
            rstEx = ex;
        }

        if(rstEx != null) {
            mapRlt = buildResult(mapRlt, mapReq, rstEx, request);
        }
        else {
            if ( !"Y".equals( CommonUtil.nvlMap(mapRlt, "status"))) {
                CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
            } else {
                CtrlUtil.settingRst(mapRlt, 200, "success", "Y");
            }
        }
        return mapRlt;
    }

    /**
     * 회원 ID의 중복을 점검
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws Exception
     */
	@GetMapping(value = "/userIdDupCheck" )
	@Operation(summary = "[메뉴 : 회원관리 > 사용자], [메뉴:강사관리 > 강사] : 회원 ID의 중복을 점검한다.", description = "회원 ID의 중복 검사" )
	@Parameters({
		 @Parameter(name = "user_id"          , required=true , description = "회원 ID", schema = @Schema(implementation = String.class))
	})
    public Map<String, Object> userIdDupCheck(HttpServletRequest request,HttpServletResponse response , RequestEntity<Map<String, Object>> requestEntity) throws Exception{

	    Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt    = new HashMap();

		Map<String, Object> resutlMap 	= new HashMap<String, Object>();

        Exception rstEx                 = null;
        try{
            Map mapRs = dbSvc.dbDetail("user.userIdDupCheck", mapReq);

            if ( mapRs == null ) {
                mapRlt.put("id_dup", "N");
                CtrlUtil.settingRst(mapRlt, 200, "사용가능한 회원ID 입니다.", "Y");
            } else {
                mapRlt.put("id_dup", "Y");
                CtrlUtil.settingRst(mapRlt, 600, "회원ID가 중복됩니다.", "N");
            }
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
	}


	/**
	 * 회원(강사)정보를 저장
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/insertUser" )
	@Operation(summary = "[메뉴 : 회원관리 > 사용자], [메뉴:강사관리 > 강사] : 회원(강사)을 insert한다.", description = "회원(강사)정보를 저장한다." )
	@Parameters({
		  @Parameter(name = "cust_no"          , required=true , description = "기관번호", schema = @Schema(implementation = Integer.class))
		, @Parameter(name = "user_id"          , required=true , description = "회원 ID", schema = @Schema(implementation = String.class))
		, @Parameter(name = "passbook_no"      , required=false, description = "통장번호", schema = @Schema(implementation = String.class))
		, @Parameter(name = "intro"            , required=false, description = "소개", schema = @Schema(implementation = String.class))
		, @Parameter(name = "email"            , required=false, description = "이메일", schema = @Schema(implementation = String.class))
		, @Parameter(name = "addr_detail"      , required=false, description = "상세주소", schema = @Schema(implementation = String.class))
		, @Parameter(name = "addr"             , required=false, description = "주소", schema = @Schema(implementation = String.class))
		, @Parameter(name = "post_no"          , required=false, description = "우편번호", schema = @Schema(implementation = String.class))
		, @Parameter(name = "fax_no"           , required=false, description = "팩스번호", schema = @Schema(implementation = String.class))
		, @Parameter(name = "tel_no"           , required=false, description = "전화번호", schema = @Schema(implementation = String.class))
		, @Parameter(name = "hp_no"            , required=false, description = "휴대전화번호", schema = @Schema(implementation = String.class))
		, @Parameter(name = "brithday"         , required=false, description = "생년월일", schema = @Schema(implementation = String.class))
		, @Parameter(name = "gender_gbn"       , required=false, description = "성별", schema = @Schema(implementation = String.class))
		, @Parameter(name = "user_passwd"      , required=false, description = "비밀번호", schema = @Schema(implementation = String.class))
		, @Parameter(name = "user_grp_cd"      , required=false, description = "회원구분코드[UGP001:수강생, UGP002:강사, UGP003:운영자]", schema = @Schema(implementation = String.class))
		, @Parameter(name = "user_nm"          , required=false, description = "회원명", schema = @Schema(implementation = String.class))
		, @Parameter(name = "sbj_grp_cd"       , required=false, description = "과목그룹코드", schema = @Schema(implementation = String.class))
		, @Parameter(name = "file_no"          , required=false, description = "사진 업로드 후 file_no", schema = @Schema(implementation = Integer.class))
	})
    public Map<String, Object> insertUser(HttpServletRequest request,HttpServletResponse response , RequestEntity<Map<String, Object>> requestEntity) throws Exception{

	    Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            Map mapRs = dbSvc.dbDetail("user.userIdDupCheck", mapReq);

           if ( mapRs == null ) {
               mapReq.put("brithday", CommonUtil.removeDateFormat(mapReq, "brithday"));
               dbSvc.dbInsert("user.insertUser", mapReq);

               if (!"".equals( CommonUtil.nvlMap(mapReq, "file_no"))) {
                   mapReq.put("ref_pk", CommonUtil.nvlMap(mapReq, "user_no"));
                   mapReq.put("ref_nm", "TB_USER"); // 테이블 명

                   dbSvc.dbUpdate("file.updateTblFile", mapReq);
               }

           } else {
               CtrlUtil.settingRst(mapRlt, 600, "회원ID가 중복됩니다.", "N");
           }
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
	}


	/**
	 * 회원을 update한다
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/updateUser" )
	@Operation(summary = "메뉴 : 시스템관리 > 사용자 : 회원을 update한다.", description = "회원정보를 수정한다." )
	@Parameters({
		  @Parameter(name = "user_no"          , required=true, description = "회원번호", schema = @Schema(implementation = Integer.class))
		, @Parameter(name = "cust_no"          , required=false, description = "기관번호", schema = @Schema(implementation = Integer.class))
		, @Parameter(name = "user_id"          , required=true, description = "회원 ID", schema = @Schema(implementation = String.class))
		, @Parameter(name = "passbook_no"      , required=false, description = "통장번호", schema = @Schema(implementation = String.class))
		, @Parameter(name = "intro"            , required=false, description = "소개", schema = @Schema(implementation = String.class))
		, @Parameter(name = "email"            , required=false, description = "이메일", schema = @Schema(implementation = String.class))
		, @Parameter(name = "addr_detail"      , required=false, description = "상세주소", schema = @Schema(implementation = String.class))
		, @Parameter(name = "addr"             , required=false, description = "주소", schema = @Schema(implementation = String.class))
		, @Parameter(name = "post_no"          , required=false, description = "우편번호", schema = @Schema(implementation = String.class))
		, @Parameter(name = "fax_no"           , required=false, description = "팩스번호", schema = @Schema(implementation = String.class))
		, @Parameter(name = "tel_no"           , required=false, description = "전화번호", schema = @Schema(implementation = String.class))
		, @Parameter(name = "hp_no"            , required=false, description = "휴대전화번호", schema = @Schema(implementation = String.class))
		, @Parameter(name = "brithday"         , required=false, description = "생년월일", schema = @Schema(implementation = String.class))
		, @Parameter(name = "gender_gbn"       , required=false, description = "성별", schema = @Schema(implementation = String.class))
		, @Parameter(name = "user_passwd"      , required=false, description = "비밀번호", schema = @Schema(implementation = String.class))
		, @Parameter(name = "user_grp_cd"      , required=false, description = "회원구분코드", schema = @Schema(implementation = String.class))
		, @Parameter(name = "user_nm"          , required=false, description = "회원명", schema = @Schema(implementation = String.class))
		, @Parameter(name = "sbj_grp_cd"       , required=false, description = "과목그룹코드", schema = @Schema(implementation = String.class))
		, @Parameter(name = "file_no"          , required=false, description = "사진 업로드 후 file_no", schema = @Schema(implementation = Integer.class))

	})
    public Map<String, Object> updateUser(HttpServletRequest request,HttpServletResponse response , RequestEntity<Map<String, Object>> requestEntity) throws Exception{

	    Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            mapReq.put("brithday", CommonUtil.removeDateFormat(mapReq, "brithday"));
            dbSvc.dbUpdate("user.updateUser", mapReq);

            if (!"".equals( CommonUtil.nvlMap(mapReq, "file_no"))) {
               mapReq.put("ref_pk", CommonUtil.nvlMap(mapReq, "user_no"));
               mapReq.put("ref_nm", "TB_USER"); // 테이블 명

               dbSvc.dbDelete("file.filePreDelete", mapReq);
               dbSvc.dbUpdate("file.updateTblFile", mapReq);
            }
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
	}

	/**
	 * 강사, 수강생, 운영자, 슈퍼관리자등 정보를 삭제
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/updateDelete" )
	@Operation(summary = "강사, 수강생, 운영자, 슈퍼관리자등 정보를 삭제한다.", description = "회원정보 삭제." )
	@Parameters({
		  @Parameter(name = "user_no"          , required=true, description = "회원번호", schema = @Schema(implementation = Integer.class))
	})
    public Map<String, Object> updateDelete(HttpServletRequest request,HttpServletResponse response , RequestEntity<Map<String, Object>> requestEntity) throws Exception{

	    Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            dbSvc.dbUpdate("user.updateUserUseYn", mapReq);
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
	}

	/**
	 * 회원의 강좌내역[Tab]
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
    @PostMapping("/userLectureList")
    @Operation(summary = "메뉴 : 회원관리 > 회원의 강좌내역[Tab]", description = "메뉴 회원상세화면의 하단에 있는 TAB 강좌내역의 목록 정보")
	@Parameters({
		@Parameter(name = "std_user_no", required = true, description = "수강생(회원) 번호", schema = @Schema(implementation = String.class))
	})
    public Map<String, Object> userLectureList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            mapRlt.put("list" , dbSvc.dbList("lecture.lectureUserList" , mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 회원의 출석이력[Tab] 목록 조회
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/userLectureAppList")
    @Operation(summary = "메뉴 : 회원관리 >회원의 출석이력[Tab]", description = "메뉴 회원상세화면의 하단에 TAB 있는 출석의 목록 정보")
	@Parameters({
		@Parameter(name = "std_user_no", required = true, description = "수강생(회원) 번호", schema = @Schema(implementation = String.class))
	})
    public Map<String, Object> userLectureAppList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            mapRlt.put("list" , dbSvc.dbList("lecture.lectureUserAppList" , mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 로그인한 정보를 기록한 정보를 조회
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/loginHistList")
    @Operation(summary = "메뉴 : 시스템 관리 > 접속로그 목록", description = "로그인한 정보를 기록한 정보를 조회한다.")
	@Parameters({
		  @Parameter(name = "user_nm", required = false, description = "로그인한 회원", schema = @Schema(implementation = String.class))
		, @Parameter(name = "fr_dt", required = false, description = "로그인한 시작일(형식:20210512)", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "to_dt", required = false, description = "로그인한 종료일(형식:20210612)", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class))
	    , @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))
	})
    public Map<String, Object> loginHistList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            CommonUtil.setPageParam(mapReq);

            mapReq.put("fr_dt", CommonUtil.removeDateFormat(mapReq, "fr_dt"));
            mapReq.put("to_dt", CommonUtil.removeDateFormat(mapReq, "to_dt"));

            mapRlt.put("list"  , dbSvc.dbList ("user.loginHistList" , mapReq));
            mapRlt.put("count" , dbSvc.dbCount("user.loginHistCount" , mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }


	//----------------------------------------------------------------------------------------------------------------------------------------------
	//----------------------------------------------------------------------------------------------------------------------------------------------
	//----------------------------------------------------------------------------------------------------------------------------------------------
	//----------------------------------------------------------------------------------------------------------------------------------------------
	//----------------------------------------------------------------------------------------------------------------------------------------------



    /**
     * 일반관리자 목록을 조회
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/managerList")
    @Operation(summary = "메뉴 : 시스템관리 > 일반관리자 목록", description = "일반관리자 목록을 조회합니다.")
    @Parameters({
    		@Parameter(name = "user_nm", required = false, description = "강사명", schema = @Schema(implementation = String.class) )
    	,   @Parameter(name = "hp_no", required = false, description = "핸드폰 번호", schema = @Schema(implementation = String.class) )
    	,   @Parameter(name = "cust_no", required = false, description = "기관번호", schema = @Schema(implementation = Integer.class) )
    	,   @Parameter(name = "user_id", required = false, description = "사용자(회원) ID", schema = @Schema(implementation = String.class) )
    	,   @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class))
    	,   @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))
    })
    public Map<String, Object>  managerList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity  )  throws JsonProcessingException
    {
   	    Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        Exception rstEx                 = null;
        try{
            mapReq.put("user_gbn_cd", "UGP006"); // 일반관리자

            CommonUtil.setPageParam(mapReq);

            mapRlt.put("list" , dbSvc.dbList ("user.userList" , mapReq));
            mapRlt.put("count", dbSvc.dbCount("user.userCount" , mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 관리자 목록을 조회
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/adminList")
    @Operation(summary = "메뉴 : 시스템관리 > 관리자 목록", description = "관리자 목록을 조회합니다.")
    @Parameters({
    		@Parameter(name = "user_nm", required = false, description = "강사명", schema = @Schema(implementation = String.class) )
    	,   @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class))
    	,   @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))
    })
    public Map<String, Object>  adminList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity  )  throws JsonProcessingException
    {
        Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt = new HashMap();

        Exception rstEx                 = null;
        try{
            mapReq.put("user_gbn_cd", "UGP004"); // 관리자

            CommonUtil.setPageParam(mapReq);

            mapRlt.put("list" , dbSvc.dbList ("user.userList" , mapReq));
            mapRlt.put("count", dbSvc.dbCount("user.userCount" , mapReq));

            CtrlUtil.settingRst(mapRlt, 200, "success", "Y");
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 권한 목록을 조회
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/authUserTargetList")
    @Operation(summary = "메뉴 : 시스템관리 > 권한 목록", description = "권한 목록을 조회합니다.")
    @Parameters({
    })
    public Map<String, Object>  authUserTargetList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity  )  throws JsonProcessingException
    {
        Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt = new HashMap();

        Exception rstEx                 = null;
        try{
            mapRlt.put("tab_user_list" , dbSvc.dbList ("user.authUserTargetList" , mapReq));

            mapReq.put("repr_cd", "UGP");
            mapRlt.put("tab_grp_list"  , dbSvc.dbList ("code.codeList" , mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 관리자 대상으로 목록 조회
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/authUserMenuList")
    @Operation(summary = "메뉴 : 시스템관리 > 메뉴 목록(관리자 대상) [", description = "관리자 대상으로 목록을 조회합니다.")
    @Parameters({
   	     @Parameter(name = "user_no", required = true, description = "회원명(관리자명)", schema = @Schema(implementation = Integer.class) )
    })
    public Map<String, Object>  authUserMenuList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity  )  throws JsonProcessingException
    {
   	    Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt = new HashMap();

        Exception rstEx                 = null;
        try{
            mapRlt.put("list" , dbSvc.dbList ("user.authUserMenuList" , mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }

    /**
     * 권한 그룹 대상으로 목록
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/authGrpMenuList")
    @Operation(summary = "메뉴 : 시스템관리 > 메뉴 목록(권한 그룹 대상) [", description = "권한 그룹 대상으로 목록을 조회합니다.")
    @Parameters({
   	     @Parameter(name = "user_grp_cd", required = true, description = "회원명(관리자명)", schema = @Schema(implementation = Integer.class) )
    })
    public Map<String, Object>  authGrpMenuList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity  )  throws JsonProcessingException
    {
   	    Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt = new HashMap();

        Exception rstEx                 = null;
        try{
            mapRlt.put("list" , dbSvc.dbList ("user.authGrpMenuList" , mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }


    /**
     * 메뉴에 대한 권한[관리자별 메뉴]을 저장등록
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
	@Operation(summary = "메뉴 : 시스템관리 > 권한[관리자별 메뉴] 등록", description = "메뉴에 대한 권한을 저장등록한다" )
	@Parameters({
		   @Parameter(name = "user_no"      , required = true , description = "관리자번호", schema = @Schema(implementation = Integer.class))
		,  @Parameter(name = "menu_id"      , required = true , description = "메뉴코드[배열]", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "acc_auth_yn"  , required = false, description = "접근권한[배열]", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "reg_auth_yn"  , required = false, description = "등록권한[배열]", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "upd_auth_yn"  , required = false, description = "수정권한[배열]", schema = @Schema(implementation = String.class))
	    ,  @Parameter(name = "mgr_auth_yn"  , required = false, description = "관리권한[배열]", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "dn_auth_yn"   , required = false, description = "다운권한[배열]", schema = @Schema(implementation = String.class))
	})
    @RequestMapping(value = "/authMenuUserSave", method = RequestMethod.POST )
    public Map<String, Object> authMenuSave(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            //해당 그룹의 권한 정보 일괄 삭제
            dbSvc.dbDelete("user.deleteMenuUserAuth", mapReq);
            // 입력된 권한정보 등록
            mapRlt = authMenuSaveWork(request, response, mapReq, "user.insertMenuUserAuth");

        }catch (Exception ex){
            rstEx = ex;
        }

        if(rstEx != null) {
            mapRlt = buildResult(mapRlt, mapReq, rstEx, request);
        }

        return mapRlt;
	}

	/**
	 * 메뉴에 대한 권한[그룹별 메뉴]을 저장등록
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
	@Operation(summary = "메뉴 : 시스템관리 > 권한[그룹별 메뉴] 등록", description = "메뉴에 대한 권한을 저장등록한다" )
	@Parameters({
		   @Parameter(name = "user_grp_cd"  , required = true , description = "그룹코드", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "menu_id"      , required = true , description = "메뉴코드[배열]", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "acc_auth_yn"  , required = false, description = "접근권한[배열]", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "reg_auth_yn"  , required = false, description = "등록권한[배열]", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "upd_auth_yn"  , required = false, description = "수정권한[배열]", schema = @Schema(implementation = String.class))
	    ,  @Parameter(name = "mgr_auth_yn"  , required = false, description = "관리권한[배열]", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "dn_auth_yn"   , required = false, description = "다운권한[배열]", schema = @Schema(implementation = String.class))
	})
    @RequestMapping(value = "/authMenuGrpSave", method = RequestMethod.POST )
    public Map<String, Object> authMenuGrpSave(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            //해당 그룹의 권한 정보 일괄 삭제
            dbSvc.dbDelete("user.deleteMenuGrpAuth", mapReq);
            // 입력된 권한정보 등록
            mapRlt = authMenuSaveWork(request, response, mapReq, "user.insertMenuGrpAuth");

        }catch (Exception ex){
            rstEx = ex;
        }

        if(rstEx != null) {
            mapRlt = buildResult(mapRlt, mapReq, rstEx, request);
        }

        return mapRlt;
	}


	/**
	 * 특정 사용자 또는 그룹에 대한 메뉴 권한 정보를 insert 처리
	 * @param request
	 * @param response
	 * @param mapReq
	 * @param strXPath
	 * @return
	 * @throws JsonProcessingException
	 */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map<String, Object> authMenuSaveWork(HttpServletRequest  request, HttpServletResponse response, Map mapReq, String strXPath)  throws JsonProcessingException {

    	Map<String, Object> mapRlt    = new HashMap();

        Exception rstEx                 = null;
        try{
            log.info("=========================================================");
            log.info("authMenuSaveWork==>" + mapReq.toString());
            log.info("=========================================================");
//                CtrlUtil.settingRst(mapRlt, 200, "success", "Y");

            List lstRs = (List)mapReq.get("list");

            if (lstRs == null || lstRs.isEmpty()) {
                return mapRlt;
            }

            for (int nLoop=0; nLoop < lstRs.size(); nLoop++){

                Map mapRs = (Map)lstRs.get(nLoop);
                // 관리자 권한 등록일 때
                mapRs.put("user_no",     CommonUtil.nvlMap(mapReq, "user_no"));
                // 그룹별 권한 등록일 때
                mapRs.put("user_grp_cd", CommonUtil.nvlMap(mapReq, "user_grp_cd"));

                log.debug("mapRs==>" + mapRs.toString());

                if ( "Y".equals(CommonUtil.nvlMap(mapRs, "acc_auth_yn")) ||
                     "Y".equals(CommonUtil.nvlMap(mapRs, "reg_auth_yn")) ||
                     "Y".equals(CommonUtil.nvlMap(mapRs, "upd_auth_yn")) ||
                     "Y".equals(CommonUtil.nvlMap(mapRs, "mgr_auth_yn")) ||
                     "Y".equals(CommonUtil.nvlMap(mapRs, "dn_auth_yn"))) {

                     dbSvc.dbInsert(strXPath, mapRs);
                }
            }
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
	}



    public Map<String, Object> authMenuSaveWork_OLDDDDDDDDDDDDDDD(HttpServletRequest  request, HttpServletResponse response, Map mapReq, String strXPath)  throws JsonProcessingException {

    	Map<String, Object> mapRlt    = new HashMap();

		try {

			System.out.println("=========================================================");
			System.out.println("authMenuSaveWork==>" + mapReq.toString());
			System.out.println("=========================================================");

			        String[] arrMenuId    = CommonUtil.getMapValArray(mapReq, "menu_id");
					String[] arrAccAuthYn = CommonUtil.getMapValArray(mapReq, "acc_auth_yn");
					String[] arrRegAuthYn = CommonUtil.getMapValArray(mapReq, "reg_auth_yn");
					String[] arrUpdAuthYn = CommonUtil.getMapValArray(mapReq, "upd_auth_yn");
					String[] arrMgrAuthYn = CommonUtil.getMapValArray(mapReq, "mgr_auth_yn");
					String[] arrDnAuthYn  = CommonUtil.getMapValArray(mapReq, "dn_auth_yn");

					CtrlUtil.settingRst(mapRlt, 200, "success", "Y");

					if ( arrMenuId == null && arrMenuId.length == 0 ) {
						CtrlUtil.settingRst(mapRlt, 400, "처리할 데이터가 없습니다.", "Y");
						return mapRlt;
					}

					if ( arrMenuId != null && arrAccAuthYn != null && arrRegAuthYn != null  && arrUpdAuthYn != null  && arrMgrAuthYn != null  && arrDnAuthYn != null  ) {

						if ( arrMenuId.length != arrAccAuthYn.length ||
							 arrMenuId.length != arrRegAuthYn.length ||
							 arrMenuId.length != arrUpdAuthYn.length ||
							 arrMenuId.length != arrMgrAuthYn.length ||
							 arrMenuId.length != arrDnAuthYn.length  ) {

						     CtrlUtil.settingRst(mapRlt, 400, "체크박스의 배열의 크기가 다릅니다.", "Y");
							 return mapRlt;
					    }
					}

                    for ( int nIdx=0; nIdx < arrAccAuthYn.length; nIdx ++)
                    {
                    	mapReq.put("menu_id"    , arrMenuId[ nIdx ] );
                    	mapReq.put("acc_auth_yn", arrAccAuthYn[ nIdx ] );
                    	mapReq.put("reg_auth_yn", arrRegAuthYn[ nIdx ] );
                    	mapReq.put("upd_auth_yn", arrUpdAuthYn[ nIdx ] );
                    	mapReq.put("mgr_auth_yn", arrMgrAuthYn[ nIdx ] );
                    	mapReq.put("dn_auth_yn" , arrDnAuthYn[ nIdx ] );




                    	dbSvc.dbInsert(strXPath, mapReq);

                    }



			}  catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {

				super.errorLogWrite(request, mapReq, e1);
				log.error(this.getClass().getName(), e1);



				CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

			}  catch (DataAccessException e2) {


				String sqlId 	= "";
				String sqlMsg 	= e2.getMessage();

				if(sqlMsg.indexOf("/*") > 0) {
					sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
					mapReq.put("sql_id", sqlId);
				}

				super.errorLogWrite(request, mapReq, e2);
				log.error(this.getClass().getName(), e2);



				CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

			}  catch (Exception e) {

				log.error(e.getMessage());

				CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
			}

	        return mapRlt;
	}


    /**
     * 로그인 사용자에 맞는 메뉴 목록 조회
     * @param request
     * @param response
     * @param requestEntity
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/userAuthMenuList")
	@Operation(summary = "사용자의 로그인 정보를 이용하여 부여된 권한의 메뉴를 표시한다.", description = "메뉴에 대한 권한" )
	@Parameters({
		    @Parameter(name = "user_no"      , required = true , description = "회원번호", schema = @Schema(implementation = String.class))
	})
    public Map<String, Object>  userAuthMenuList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity  )  throws JsonProcessingException
    {
        Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt = new HashMap();

        Exception rstEx                 = null;
        try{
            if ( "".equals( CommonUtil.nvlMap(mapReq, "user_no"))) {
                mapReq.put("user_no", CommonUtil.nvlMap(mapReq, "token_user_no") );
            }

            mapRlt.put("list" , dbSvc.dbList ("user.userGrpMenuList" , mapReq));
        }catch (Exception ex){
            rstEx = ex;
        }
        return buildResult(mapRlt, mapReq, rstEx, request);
    }


}

