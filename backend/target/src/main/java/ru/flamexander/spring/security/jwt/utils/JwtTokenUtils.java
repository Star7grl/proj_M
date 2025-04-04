package ru.flamexander.spring.security.jwt.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
// ОБЪЯВЛЯЕТ КЛАСС И ИНИЦИАЛИЗИУЕТ ДВА ЕГО ПОЛЯ С ИСПОЛЬЗОВАНИЕМ АННОТАЦИИ @Value И Spring Framework
public class JwtTokenUtils {

    // поле типа String предназначено для хранения секретного ключа
    @Value("${jwt.secret}")
    private String secret;

    // поле типа Duration хранит срок жизни
    @Value("${jwt.lifetime}")
    private Duration jwtLifetime;


    // ГОТОВИТ ДАННЫЕ ДЛЯ ГЕНЕРАЦИИ JWT(JSON WEB TOKEN), ДОБАВЛЯЯ ИНФОРМЦИЮ О РОЛЯХ И ПОЛЬЗОВАТЕЛЯ В КАЧЕСТВЕ ПРЕТЕНЗИЙ К ТОКЕНУ
    public String generateToken(UserDetails userDetails) { // создаем пустой хэш-мап, который используется для хранения претензий(доп.данные, связанные с токеном, которые могут использоваться
        // для проверки авторизации и представления дотупа к различным ресурсам
        Map<String, Object> claims = new HashMap<>();
        List<String> rolesList = userDetails.getAuthorities().stream() //извекает список ролей пользователя и преобразует его в список строк
                // userDetails.getAuthorities() - предполагается, что userDetails это объект, содержащий информацю о пользователе.
                // метод getAuthorities() возвразщает коллекцию объектов 'GrantedAuthority', каждый из которых представляет собой роль пользователя
                // stream() поток из коллекции 'GrantedAuthority'
                .map(GrantedAuthority::getAuthority) // каждый элемент потока
                .collect(Collectors.toList()); // результат преобразования, содержащий имена ролей пользователя в виде строк
        claims.put("roles", rolesList); // список ролей. содержит информацию о ролях пользователя, которая будет включена в генерируемый JWT.


        // КОД ГЕНЕРИРУЕТ JWT, СОДЕРЖАЩУЮ ИНФОРМАЦИЮ О ПОЛЬЗОВАТЕЛЕ(ИМЯ, ВРЕМЯ ВЫДАЧИ, ВРЕМЯ ИСТЕЧЕНИЯ, ДОП.ПРЕТЕНЗИИ),
        // ПОДПИСАННЫЙ СЕКРЕТНЫМ КЛЮЧОМ
        Date issuedDate = new Date(); // создает объект Date
        Date expiredDate = new Date(issuedDate.getTime() + jwtLifetime.toMillis()); // вычисляет время истечения действия JWT
        return Jwts.builder() // основная часть, возвращает JWT
                .setClaims(claims) // добавляет пользовательские претензии
                .setSubject(userDetails.getUsername()) // устанавливает субъект
                .setIssuedAt(issuedDate) // выдает время выдачи токена
                .setExpiration(expiredDate) // устанавливает время истечения токена
                .signWith(SignatureAlgorithm.HS256, secret) //подписывает JWT с использованием алгоритма подписи и секретный ключ
                .compact(); // компактирует JWT в компактную строковую форму, готовую к использованию.
    }

    // ИЗВЛЕКАЕТ ИМЯ ПОЛЬЗОВАТЕЛЯ ИЗ JWT
    public String getUsername(String token) { // объявление публичного метода getUsername, который принимает строку токен в качестве аргумента и
        // позвращает строку, предтавляющую имя пользователя
        return getAllClaimsFromToken(token).getSubject();
        //getAllClaimsFromToken(token) - вызов метода, принимающего JWT в виде строки и возвращает объект содержащий все претензии из этого токена
        //getSubject() - метод объекта. извлекает значение поля 'subject' из JWT
    }


    // ИЗВЛЕКАЕТ СПИСОК РОЛЕЙ
    public List<String> getRoles(String token) { // объявление публичного метода `getRoles`, который принимает JWT (строку `token`) в качестве
        // входного параметра и возвращает список строк (`List<String>`), представляющих роли, присвоенные пользователю
        return getAllClaimsFromToken(token).get("roles", List.class);
        // getAllClaimsFromToken(token) - вызов метода, который парсит JWT и возвращает объект `Claims` из библиотеки JJWT
        // get("roles", List.class) метод, который ивлекает значение претензии с ключом 'roles' из объекста 'Claims', 'List.class' указывает на ожидаемый тип данных
        // — список (`List`)
    }


    // ПАРСИНГ (преобразование строк для чтения информации из консоли, файла или других источников) JWT И ИЗВЛЕЧЕНИЕ ИЗ НЕГО ПРЕТЕНЗИЙ
    private Claims getAllClaimsFromToken(String token) { // объявление приватного метода `getAllClaimsFromToken`, который принимает JWT в виде строки (`token`)
        // и возвращает объект `Claims`, содержащий все претензии (claims) из этого токена.
        // Приватный модификатор доступа (`private`) означает, что этот метод доступен только внутри класса, в котором он определен.
        return Jwts.parser() // Создает экземпляр парсера JWT из библиотеки JJWT
                .setSigningKey(secret) // Устанавливает секретный ключ (`secret`), который использовался для подписи JWT.
                // Этот ключ необходим для проверки подписи токена и подтверждения его подлинности. Если подпись неверна, будет брошено исключение.
                .parseClaimsJws(token) // Парсит предоставленный JWT (`token`), используя установленный секретный ключ для проверки подписи.
                // Метод `parseClaimsJws` возвращает объект `Jws<Claims>`, который содержит подписанную часть JWT и объект `Claims`.
                .getBody(); //Извлекает объект `Claims` из `Jws<Claims>`. Объект `Claims` содержит все претензии (claims) из JWT,
                // такие как имя пользователя, роли, время выдачи и истечения срока действия и т.д.
    }
}
