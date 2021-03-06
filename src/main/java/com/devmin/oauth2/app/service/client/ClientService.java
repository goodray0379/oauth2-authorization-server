package com.devmin.oauth2.app.service.client;

import com.devmin.oauth2.app.domain.client.Client;
import com.devmin.oauth2.app.domain.client.ClientRepository;
import com.devmin.oauth2.app.domain.user.User;
import com.devmin.oauth2.app.web.dto.client.ClientResponseDto;
import com.devmin.oauth2.app.web.dto.client.ClientSaveRequestDto;
import com.devmin.oauth2.app.web.dto.client.ClientSaveResponseDto;
import com.devmin.oauth2.common.config.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ClientService{
    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public ClientSaveResponseDto save(ClientSaveRequestDto clientSaveRequestDto, User user){
        //Client ID와 Secret 정보 생성
        Map<String, String> clientInfos = new HashMap<>();
        clientInfos.put("clientId", createClientId());
        clientInfos.put("clientSecret", createClientSecret());
        Client entity= clientSaveRequestDto.toEntity(clientInfos, user);

        return new ClientSaveResponseDto(clientRepository.save( entity ));
    }

    public ClientResponseDto findById(Long id){
        Client client =  clientRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("해당 클라이언트가 없습니다. id=" + id));
        return new ClientResponseDto(client);
    }

    public List<ClientResponseDto> findByUsername(User user){
        List<Client> clientList =  clientRepository.findAllByUser(user);
        List<ClientResponseDto> ClientResponseDtoList =
                clientList.stream().map(client -> modelMapper.map(client, ClientResponseDto.class)).collect(Collectors.toList());
        return ClientResponseDtoList;
    }

//    public UserLoginResponseDto login(User entity, UserLoginRequestDto userLoginRequestDto){
//        String authorizationCode = UUID.randomUUID().toString();
//        //After To Do: 인증코드 저장 로직 추가해야함
//        return new UserLoginResponseDto(authorizationCode, userLoginRequestDto.getState());
//    }

//    public UserLoginResponseDto createToken(User entity){
//        //토큰 발급
//        Map<String, String> tokens = new HashMap<>();
//        tokens.put("accessToken", jwtTokenProvider.createAccessToken(entity));
//        tokens.put("refreshToken", jwtTokenProvider.createRefreshToken(entity));
//
//        entity.updateRefreshToken(tokens.get("refreshToken"));
//        return new UserLoginResponseDto(entity, tokens);
//    }

    public String createClientId(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    public String createClientSecret(){
        return UUID.randomUUID().toString().split("-")[0];
    }
}
