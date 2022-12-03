package kr.co.bigsnow.api.jwt;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    @Value("${spring.jwt.secret}")
    private String key;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("user_id".equals(username)) {
            return new User("user_id", "$2a$10$jCvWm3NXDRFs/EfuI4h4.u0ZxNocv.ZkgEy6qbjVXrfQ5.KzLfhAe",
                    new ArrayList<>());

        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

    /**
     * 토큰생성
     * : (유효기간을 기본 24시간으로 저정된 함수)
     * 
     * @param strUserNo
     * @param strUserNm
     * @param strUserId
     * @param strCustNo
     * @param strUsrGrpCd
     * @return
     */
    public String createToken(String strUserNo, String strUserNm, String strUserId, String strCustNo,
            String strUsrGrpCd) {
        Long expiredTime = 1000 * 60L * 60L * 24L; // 토큰 유효 시간 (24시간)

        return createTokenCommon(strUserNo, strUserNm, strUserId, strCustNo, strUsrGrpCd, expiredTime);
    }

    /**
     * 토클 생성
     * : 유효기간을 날짜 단위로 지정해서 생성
     * 
     * @param strUserNo
     * @param strUserNm
     * @param strUserId
     * @param strCustNo
     * @param strUsrGrpCd
     * @param expireDay
     * @return
     */
    public String createTokenExpireDay(String strUserNo, String strUserNm, String strUserId, String strCustNo,
            String strUsrGrpCd, int expireDay) {

        Long expiredTime = 1000 * 60L * 60L * 24L; // 토큰 유효 시간 (24시간)

        expiredTime = expiredTime * Long.valueOf(Integer.valueOf(expireDay));

        return createTokenCommon(strUserNo, strUserNm, strUserId, strCustNo, strUsrGrpCd, expiredTime);
    }

    /**
     * 토큰 생성 공통
     * 
     * @param strUserNo
     * @param strUserNm
     * @param strUserId
     * @param strCustNo
     * @param strUsrGrpCd
     * @param expiredTime // 토큰 유효 시간
     * @return
     */
    private String createTokenCommon(
            String strUserNo, String strUserNm, String strUserId, String strCustNo, String strUsrGrpCd, Long expiredTime) {

        // Header 부분 설정
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        // payload 부분 설정
        Map<String, Object> payloads = new HashMap<>();
        payloads.put("user_no", strUserNo);
        payloads.put("user_id", strUserId);
        payloads.put("user_nm", strUserNm);
        payloads.put("cust_no", strCustNo);
        payloads.put("user_grp_cd", strUsrGrpCd);

        // Long expiredTime = 1000 * 60L * 60L * 24L; // 토큰 유효 시간 (24시간)

        Date ext = new Date(); // 토큰 만료 시간
        ext.setTime(ext.getTime() + expiredTime);

        // 토큰 Builder
        String jwt = Jwts.builder()
                .setHeader(headers) // Headers 설정
                .setClaims(payloads) // Claims 설정
                .setSubject("user") // 토큰 용도
                .setExpiration(ext) // 토큰 만료 시간 설정
                .signWith(SignatureAlgorithm.HS256, key.getBytes()) // HS256과 Key로 Sign
                .compact(); // 토큰 생성

        System.err.println("token:" + jwt );

        return jwt;
    }

    /**
     * 토큰 검증
     * 
     * @param jwt
     * @return
     * @throws UnsupportedEncodingException
     */
    public Map<String, Object> verifyJWT(String jwt) throws UnsupportedEncodingException {
        Map<String, Object> claimMap = null;
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key.getBytes("UTF-8")) // Set Key
                    .parseClaimsJws(jwt) // 파싱 및 검증, 실패 시 에러
                    .getBody();

            claimMap = claims;

            Date expiration = claims.get("exp", Date.class);
            claimMap.put("expDate", expiration);

            // String data = claims.get("data", String.class);
            // log.info("##### claims :" + claimMap);
            // log.info("##### espiration date format : " + ((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")).format(expiration)));

        } catch (ExpiredJwtException e) { // 토큰이 만료되었을 경우
            System.out.println(e);

        } catch (Exception e) { // 그외 에러났을 경우
            System.out.println(e);

        }
        return claimMap;
    }

}