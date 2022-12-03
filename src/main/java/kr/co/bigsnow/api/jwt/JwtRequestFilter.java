package kr.co.bigsnow.api.jwt;

import java.io.IOException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import kr.co.bigsnow.core.util.CommonUtil;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader("Authorization");

		String strUrl = CommonUtil.getCurrentUrl( request );

		System.out.println("[JwtRequestFilter] strUrl:" + strUrl);

		String username = null;
		String jwtToken = null;

		System.out.println("Authorization:" + requestTokenHeader);

		if (   requestTokenHeader != null) {
			 Map<String, Object>  mapToken = jwtUserDetailsService.verifyJWT(requestTokenHeader);


			 if( mapToken != null) {

				    UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername("user_id");

					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken( userDetails, null, userDetails.getAuthorities());
					usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

					System.out.println("mapToken:" + mapToken.toString());

			 } else {
				 System.out.println("mapToken:실패");
			 }


		}



		chain.doFilter(request, response);
	}


}