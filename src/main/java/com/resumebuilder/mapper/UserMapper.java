package com.resumebuilder.mapper;

import com.resumebuilder.dto.response.UserResponse;
import com.resumebuilder.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "fullName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    UserResponse toUserResponse(User user);

    @Named("idToString")
    default String idToString(java.util.UUID id) {
        return id != null ? id.toString() : null;
    }
}