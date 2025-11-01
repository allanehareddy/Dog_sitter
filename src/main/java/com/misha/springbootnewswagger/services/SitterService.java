package com.misha.springbootnewswagger.services;

import com.misha.springbootnewswagger.dto.*;
import com.misha.springbootnewswagger.entities.SitterEntity;
import com.misha.springbootnewswagger.exceptions.ResourceNotFoundException;
import com.misha.springbootnewswagger.repositories.SitterRepository;
import com.misha.springbootnewswagger.utils.GeometryUtil;
import com.misha.springbootnewswagger.utils.ImageHandler;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SitterService {

    private final ModelMapper modelMapper;
    private final ImageHandler imageHandler;
    private final SitterRepository sitterRepository;
    private final PasswordEncoder passwordEncoder;

    public List<SitterResponse> getSittersWithinRadius(LocationDto locationDto) {
        double radiusInMeters = locationDto.getRadiusInKm() * 1000;

        String centerPointWKT = String.format("POINT(%s %s)", locationDto.getLatitude(), locationDto.getLongitude());

        List<SitterEntity> sitterEntity = sitterRepository.findSittersWithinRadius(centerPointWKT, radiusInMeters);

        return sitterEntity
                .stream()
                .map(this::mapSitterToSitterResponse)
                .collect(Collectors.toList());
    }

    public SitterResponse mapSitterToSitterResponse(SitterEntity sitterEntity) {
        SitterResponse sitterResponse = new SitterResponse();
        sitterResponse.setId(sitterEntity.getId());
        sitterResponse.setContactName(sitterEntity.getContactName());
        sitterResponse.setEmail(sitterEntity.getEmail());
        sitterResponse.setCompanyName(sitterEntity.getCompanyName());
        sitterResponse.setAddress(sitterEntity.getAddress());
        sitterResponse.setLocation(modelMapper.map(sitterEntity.getLocation(), PointDto.class));
        sitterResponse.setTimeOfOpening(sitterEntity.getTimeOfOpening());
        sitterResponse.setTimeOfClosing(sitterEntity.getTimeOfClosing());
        sitterResponse.setChargesPerHour(sitterEntity.getChargesPerHour());
        sitterResponse.setEnable(sitterEntity.getEnable());
        sitterResponse.setLogo(imageHandler.getImageAsString(sitterEntity.getLogo()));
        sitterResponse.setRole(sitterEntity.getRole());
        return sitterResponse;
    }

    public SitterResponse createNewSitter(SitterDto sitterDto, String imageName) {
        SitterEntity toSaveEntity = modelMapper.map(convertSitterDtoToSitterRequest(sitterDto), SitterEntity.class);

        toSaveEntity.setLogo(imageName);

        SitterEntity savedSitterEntity = sitterRepository.save(toSaveEntity);

        return mapSitterToSitterResponse(savedSitterEntity);
    }

    private SitterRequest convertSitterDtoToSitterRequest(SitterDto sitterDto) {
        SitterRequest sitterRequest = new SitterRequest();
        sitterRequest.setContactName(sitterDto.getContactName());
        sitterRequest.setEmail(sitterDto.getEmail());
        sitterRequest.setPassword(passwordEncoder.encode(sitterDto.getPassword()));
        sitterRequest.setCompanyName(sitterDto.getCompanyName());
        sitterRequest.setAddress(sitterDto.getAddress());
        sitterRequest.setLocation(GeometryUtil.createPoint(sitterDto.getLatitude(), sitterDto.getLongitude()));
        sitterRequest.setTimeOfOpening(sitterDto.getTimeOfOpening());
        sitterRequest.setTimeOfClosing(sitterDto.getTimeOfClosing());
        sitterRequest.setChargesPerHour(sitterDto.getChargesPerHour());
        sitterRequest.setEnable(sitterDto.getEnable());
        sitterRequest.setRole(sitterDto.getRole());
        return sitterRequest;
    }

    public boolean deleteSitterById(Long sitterId) {
        isExistsByUserId(sitterId);
        Optional<SitterEntity> userOptional = sitterRepository.findById(sitterId);
        if (userOptional.isPresent()) {
            SitterEntity sitter = userOptional.get();
            imageHandler.deleteImage(sitter.getLogo());
        } else {
            throw new ResourceNotFoundException("User not found with id " + sitterId);
        }
        sitterRepository.deleteById(sitterId);
        return true;
    }

    public void isExistsByUserId(Long userId) {
        boolean exists = sitterRepository.existsById(userId);
        if(!exists) throw new ResourceNotFoundException("User not found with id "+userId);
    }

    @Transactional
    public SitterResponse updateSitterById(SitterDto sitterDto, Long sitterId, MultipartFile image) {
        isExistsByUserId(sitterId);
        SitterEntity toUpdateEntity = modelMapper.map(convertSitterDtoToSitterRequest(sitterDto), SitterEntity.class);
        if (image != null && !image.isEmpty()) {
            try {
                String updatedImage = imageHandler.updateImage(toUpdateEntity.getLogo(), image);
                toUpdateEntity.setLogo(updatedImage);
                System.out.println("Successfully updated image for sitter ID {}" + sitterId);
            } catch (Exception ioException) {
                System.out.println("Failed to update image for sitter ID {}: {}" + sitterId + ioException.getMessage());
            }
        }
        toUpdateEntity.setId(sitterId);
        SitterEntity updatedSitterEntity = sitterRepository.save(toUpdateEntity);
        return mapSitterToSitterResponse(updatedSitterEntity);
    }

    public boolean isEmailAlreadyExisting(String email) {
        return sitterRepository.existsByEmail(email);
    }

    public Page<SitterResponse> getFilteredSitters(Integer pageNo,
                                                 Integer pageSize,
                                                 String contactName,
                                                 String companyName,
                                                 Double charges,
                                                 String sortBy,
                                                 String order) {
        Sort sort = Sort.by(Sort.Direction.fromString(order), sortBy);
        PageRequest pageReq = PageRequest.of(pageNo, pageSize, sort);
        Page<SitterEntity> userEntityPage = sitterRepository.filterSitters(contactName, companyName, charges, pageReq);

        return userEntityPage.map(this::mapSitterToSitterResponse);
    }

    public SitterResponse getUser(String email) {
        SitterEntity user = sitterRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
        return mapSitterToSitterResponse(user);
    }
}
