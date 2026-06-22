package com.sistemabarberia.fadex_backend.modules.ia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class IaClienteService {

    @Value("${ia.microservicio.url:http://localhost:8000}")
    private String iaUrl;

    @Autowired
    private RestTemplate restTemplate;

    public Map<String, Object> analizar(MultipartFile foto, Integer idCliente) throws IOException {

        // Convertir el archivo a ByteArrayResource para que RestTemplate
        // pueda enviarlo como multipart con el nombre de campo correcto
        ByteArrayResource fotoResource = new ByteArrayResource(foto.getBytes()) {
            @Override
            public String getFilename() {
                return foto.getOriginalFilename();
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("foto", fotoResource);
        body.add("id_cliente", idCliente);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(iaUrl + "/analizar", request, Map.class);
    }


    public Map<String, Object> guardarFeedback(Integer clientId, Integer haircutId, Boolean liked, Integer rating) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("haircut_id", haircutId);
        body.add("liked", liked);
        body.add("rating", rating);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(iaUrl + "/feedback", request, Map.class);
    }


}