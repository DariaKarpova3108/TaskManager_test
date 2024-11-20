package com.example.app.mappers;

import com.example.app.dto.role.RoleDTO;
import com.example.app.models.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class RoleMapper {
    public abstract RoleDTO map(Role model);
}
