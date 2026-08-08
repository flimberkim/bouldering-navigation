# Spring Security를 추가한 뒤 프론트엔드에서의 모든 API 호출이 CORS 에러로 실패함

- 날짜: 2026-08-09
- 관련 커밋: 인증 기반(회원가입/로그인/JWT) 최초 구현

## 증상

인증 기능을 추가하기 위해 `spring-boot-starter-security`와 `SecurityConfig`를 도입한 뒤, 브라우저(React 프론트엔드)에서 보내는 모든 `/api/**` 요청이 실패했다. `curl`로는 정상 동작했지만 실제 브라우저에서는 회원가입뿐 아니라 기존에 잘 되던 검색(`GET /api/mountains`)까지 CORS 에러로 막혔다.

## 원인

CORS 프리플라이트(`OPTIONS`) 요청이 Spring Security의 `authorizeHttpRequests` 단계에서 `401`로 차단됐다. `config/WebConfig.java`가 `WebMvcConfigurer.addCorsMappings(...)`로 CORS를 설정하고 있었는데, 이 설정은 Spring MVC(DispatcherServlet) 레벨에서만 적용된다. 반면 Spring Security의 필터 체인은 DispatcherServlet **이전**에 실행되므로, `OPTIONS` 프리플라이트 요청이 MVC의 CORS 처리에 도달하기도 전에 Security의 `authorizeHttpRequests` 규칙(`permitAll`이 아닌 `OPTIONS` 메서드는 어떤 규칙에도 안 걸리고 `anyRequest().authenticated()`로 떨어짐)에 걸려 401로 거부됐다. `curl`로 테스트했을 때 못 잡아낸 이유: `curl`은 브라우저가 아니므로 프리플라이트를 보내지 않는다 — 이 문제는 실제 브라우저(또는 브라우저의 CORS 동작을 흉내내는 도구)로만 재현된다.

## 해결

CORS 설정을 Spring MVC 전용(`addCorsMappings`)에서 `CorsConfigurationSource` 빈으로 바꾸고(`config/WebConfig.java`), `SecurityConfig`의 `HttpSecurity`에 `.cors(Customizer.withDefaults())`를 추가했다. 이렇게 하면 Spring Security가 필터 체인 앞단에서 이 빈을 사용해 프리플라이트 요청에 직접 CORS 헤더를 붙여 응답하고, 프리플라이트 요청은 `authorizeHttpRequests`까지 내려가지 않는다.

```java
// config/WebConfig.java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:5173"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    configuration.setAllowedHeaders(List.of("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", configuration);
    return source;
}

// config/SecurityConfig.java
http.csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())   // CorsConfigurationSource 빈을 자동으로 찾아 사용
        ...
```

## 재발 방지를 위해 확인할 것

- **Spring Security가 있는 프로젝트에서 CORS 설정은 항상 `SecurityConfig`(`.cors(...)`)에서 관리한다.** `WebMvcConfigurer.addCorsMappings`는 이제 쓰지 않는다 — Security 필터 체인이 먼저 실행되기 때문에 MVC 레벨 CORS 설정은 프리플라이트를 못 잡는다.
- **인증/인가가 걸린 API를 브라우저 클라이언트와 함께 검증할 때는 `curl`만으로 충분하지 않다.** `curl`은 프리플라이트를 보내지 않으므로 CORS 관련 문제를 절대 재현하지 못한다 — 실제 브라우저(또는 이에 준하는 도구)로 반드시 확인한다.
- 허용 오리진(`http://localhost:5173`)이 바뀌면(배포 도메인 추가 등) `WebConfig.corsConfigurationSource()`를 갱신해야 한다 — 이전에는 `WebConfig`만 보면 됐지만 지금도 여전히 같은 파일이다.

## 관련 문서

없음 (CORS+Security 조합 규칙이 반복되면 별도 규칙 문서로 승격 검토).
