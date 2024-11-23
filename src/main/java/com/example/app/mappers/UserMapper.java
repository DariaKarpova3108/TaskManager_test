package com.example.app.mappers;

import com.example.app.dto.role.RoleDTO;
import com.example.app.dto.user.UserCreateDTO;
import com.example.app.dto.user.UserDTO;
import com.example.app.dto.user.UserUpdateDTO;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.models.Role;
import com.example.app.models.User;
import com.example.app.repositories.RoleRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class, RoleMapper.class, TaskMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class UserMapper {

    @Autowired
    private RoleRepository roleRepository;

    @Mapping(target = "passwordDigest", source = "password")
    public abstract User map(UserCreateDTO createDTO);

    public abstract UserDTO map(User model);

    @Mapping(target = "passwordDigest", source = "password")
    @Mapping(target = "roles", source = "roles", qualifiedByName = "updateRoles")
    public abstract void update(UserUpdateDTO updateDTO, @MappingTarget User model);

    @Named("updateRoles")
    public Set<Role> updateRoles(JsonNullable<Set<RoleDTO>> rolesDTO) {
        Set<Role> updateRoles = new HashSet<>();
        if (rolesDTO.isPresent()) {
            Set<RoleDTO> updateRolesDTO = rolesDTO.get();
            updateRoles = updateRolesDTO.stream()
                    .map(RoleDTO::getRoleName)
                    .map(roleName -> roleRepository.findByRoleName(roleName)
                            .orElseThrow(() -> new ResourceNotFoundException("Role with name: "
                                    + roleName + " not found")))
                    .collect(Collectors.toSet());

        }
        return updateRoles;
    }
}
