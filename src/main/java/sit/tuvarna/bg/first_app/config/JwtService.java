package sit.tuvarna.bg.first_app.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService {
    //трябва ми тайния ключ за да създам и чета JWT
    //ако не е зададено в application.properties му дава "secret-key" като default стойност
    //@Value("${security.jwt.token.secret-key:secret-key}")
    //private String secretKey;
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    private SecretKey key;

    public JwtService(){
        byte[] keyBytes = Base64.getDecoder().decode(SECRET.getBytes(StandardCharsets.UTF_8));
        key = new SecretKeySpec(keyBytes,"HmacSHA256");
    }



    //Създаване на token без extra claims
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }

    //Създаване на token с екстра claims
    private String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(claims)//setClaims deprecated
                .subject(userDetails.getUsername())//setSubject deprecated
                .issuedAt(new Date(System.currentTimeMillis())) //setIssuedAt deprecated
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))//setExpiration deprecated
                //ще бъде валиден 30 минути
                .signWith(key)//, SignatureAlgorithm.HS256
                .compact();
    }





    public String extractUsername(String token) {
        //Subject обикновенно е Username в различните настройки на user може да е email
        //За мене е username
        return  extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        /*return Jwts
                .parser()
                //.verifyWith((PublicKey) getSignInKey())
                .setSigningKey(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
                //.getBody();*/
        return Jwts
                .parser()
                .verifyWith(key)//.setSigningKey(getSignInKey()) deprecated да намеря как да го заменя
                .build().parseSignedClaims(token).getPayload();
        //Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private SecretKey getSignInKey(){
        byte[] keyBytes = Base64.getDecoder().decode(SECRET.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(keyBytes,"HmacSHA256");
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
