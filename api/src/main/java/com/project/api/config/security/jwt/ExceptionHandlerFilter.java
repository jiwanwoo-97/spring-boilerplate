package com.project.api.config.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.project.api.model.common.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            //토큰의 유효기간 만료
            setErrorResponse(response, 401, "토큰이 만료되었습니다.");
        } catch (JwtException |
                 IllegalArgumentException e) {
            //유효하지 않은 토큰
            setErrorResponse(response, 403, "권한 정보가 없는 토큰입니다.");
        }
    }

    private void setErrorResponse(
            HttpServletResponse response,
            int code, String message
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            response.setStatus(code);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse(code, message)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}