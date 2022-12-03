package kr.co.bigsnow.api.controller;

import java.util.HashMap;
import java.util.List;
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
import kr.co.bigsnow.core.controller.StandardController;
import lombok.extern.slf4j.Slf4j;

import io.swagger.annotations.Api;

@Api(tags = {"2. Menu"})
@Slf4j
@RestController
 
@RequestMapping(path = "/menu", produces = MediaType.APPLICATION_JSON_VALUE)
public class MenuController extends StandardController {
	
	/**
	 * TransactionManager
	 */
	@Autowired
	protected PlatformTransactionManager txManager;

	/**
	 * 메뉴 목록 조회
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("/menuList")
    @Operation(summary = "메뉴 : 시스템 관리 > 메뉴관리", description = "메뉴를 조회합니다.")
    public Map<String, Object> menuList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {

		Map<String, Object> mapReq      = super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt      = new HashMap();

    	List<Map<String, Object>> lstRs = null;
    	Exception rstEx                 = null;
    	try{
			lstRs         = dbSvc.dbList("menu.menuList" , mapReq);
			int nRowCount = dbSvc.dbInt ("menu.menuCount", mapReq);

			mapRlt.put("list" , lstRs);
			mapRlt.put("count", nRowCount);
		}catch (Exception ex){
			rstEx = ex;
		}

    	return buildResult(mapRlt, mapReq, rstEx, request);
    }

	/**
	 * 메뉴 상세 내용 조회
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("/menuDetail")
    @Operation(summary = "메뉴 : 시스템 관리 > 메뉴 관리(메뉴 상세조회)", description = "메뉴의 상세 조회합니다.")
    @Parameters({
     	  @Parameter(name = "menu_id", required = true, description = "메뉴 ID", schema = @Schema(implementation = Integer.class))
    })
	public Map<String, Object> menuDetail(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt = new HashMap();

		Exception rstEx            = null;
    	try {
    		mapRlt.put("detail" , dbSvc.dbDetail("menu.menuDetail" , mapReq));
		}  catch (Exception ex) {
			rstEx = ex;
		}
		return buildResult(mapRlt, mapReq, rstEx, request);
    }

	/**
	 * 메뉴 신규 정보 등록 처리
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("/insertMenu")
	@Operation(summary = "메뉴 : 시스템 관리 > 메뉴관리[저장]", description = "메뉴를 등록합니다.")
	@Parameters({
	      @Parameter(name = "menu_nm", required = true, description = "메뉴명", schema = @Schema(implementation = String.class))		
	    , @Parameter(name = "menu_depth", required = true, description = "메뉴 Depth", schema = @Schema(implementation = Integer.class))
	    , @Parameter(name = "menu_url",  required = true, description = "메뉴 URL", schema = @Schema(implementation = String.class))  
	    , @Parameter(name = "etc"     ,  required = false, description = "비고", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "url_role",  required = false, description = "URL 규칙", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "use_yn"     , required = true, description = "사용여부", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "up_menu_id" , required = false, description = "상위메뉴 ID", schema = @Schema(implementation = String.class))
	})
    public Map<String, Object> insertMenu (HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
       
		Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt  = new HashMap();

		Exception rstEx             = null;
		try {
			dbSvc.dbInsert("menu.insertMenu", mapReq);
		}  catch (Exception ex) {
			rstEx = ex;
		}
		return buildResult(mapRlt, mapReq, rstEx, request);
	}

	/**
	 * 메뉴 정보 수정 처리
	 * @param request
	 * @param response
	 * @param requestEntity
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("/updateMenu")
	@Operation(summary = "메뉴 : 시스템 관리 > 메뉴관리[수정]", description = "메뉴를 등록합니다.")
	@Parameters({
		  @Parameter(name = "menu_id", required = true, description = "메뉴 ID", schema = @Schema(implementation = Integer.class))
	    , @Parameter(name = "menu_nm", required = true, description = "메뉴명", schema = @Schema(implementation = String.class))		
	    , @Parameter(name = "menu_depth", required = true, description = "메뉴 Depth", schema = @Schema(implementation = Integer.class))
	    , @Parameter(name = "menu_url",  required = true, description = "메뉴 URL", schema = @Schema(implementation = String.class))  
	    , @Parameter(name = "etc"     ,  required = false, description = "비고", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "url_role",  required = false, description = "URL 규칙", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "use_yn"     , required = true, description = "사용여부", schema = @Schema(implementation = String.class))
	})
    public Map<String, Object> updateMenu (HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
       
		Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt  = new HashMap();

		Exception rstEx             = null;
		try {
			dbSvc.dbInsert("menu.updateMenu", mapReq);
		}  catch (Exception ex) {
			rstEx = ex;
		}
		return buildResult(mapRlt, mapReq, rstEx, request);
	}    	
	        
}
