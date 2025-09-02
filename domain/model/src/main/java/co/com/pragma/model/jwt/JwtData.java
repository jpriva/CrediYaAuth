package co.com.pragma.model.jwt;

public record JwtData (String subject, String role, Integer roleId, String name, String idNumber){
}