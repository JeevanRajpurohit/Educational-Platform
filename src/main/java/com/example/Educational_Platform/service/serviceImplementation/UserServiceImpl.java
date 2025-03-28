package com.example.Educational_Platform.service.serviceImplementation;

import com.amazonaws.services.dynamodbv2.datamodeling.ScanResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.example.Educational_Platform.Utils.PaginationResponse;
import com.example.Educational_Platform.dtos.UserDto;
import com.example.Educational_Platform.model.User;
import com.example.Educational_Platform.repository.UserRepository;
import com.example.Educational_Platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDto getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto updateUser(String id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (userDetails.getName() != null) {
            user.setName(userDetails.getName());
        }
        if (userDetails.getDob() != null) {
            user.setDob(userDetails.getDob());
        }
        if (userDetails.getGender() != null) {
            user.setGender(userDetails.getGender());
        }

        user.setUpdatedAt(new Date());
        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserDto.class);
    }

    @Override
    public PaginationResponse getAllUsersByRole(String role, Integer limit, String lastEvaluatedKey) {
        Map<String, AttributeValue> exclusiveStartKey = getExclusiveStartKey(lastEvaluatedKey);
        ScanResultPage<User> scanResult = userRepository.findByRolePaginated(role, limit, exclusiveStartKey);

        List<UserDto> users = scanResult.getResults().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());

        String nextKey = scanResult.getLastEvaluatedKey() != null ?
                scanResult.getLastEvaluatedKey().get("id").getS() : null;

        return new PaginationResponse(
                users,
                nextKey,
                limit,
                scanResult.getLastEvaluatedKey() != null
        );
    }

    private Map<String, AttributeValue> getExclusiveStartKey(String lastEvaluatedKey) {
        if (lastEvaluatedKey == null || lastEvaluatedKey.isEmpty()) {
            return null;
        }
        Map<String, AttributeValue> exclusiveStartKey = new HashMap<>();
        exclusiveStartKey.put("id", new AttributeValue().withS(lastEvaluatedKey));
        return exclusiveStartKey;
    }
}