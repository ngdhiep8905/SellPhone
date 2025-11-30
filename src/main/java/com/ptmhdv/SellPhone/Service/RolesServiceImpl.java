package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Roles;
import com.ptmhdv.SellPhone.Repository.RolesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolesServiceImpl implements RolesService {
    private final RolesRepository rolesRepository;

    @Override
    public Roles create(Roles role) {
        return rolesRepository.save(role);
    }

    @Override
    public Roles update(Integer id, Roles updated) {
        Roles exist = getById(id);
        exist.setRoleName(updated.getRoleName());
        exist.setRoleDescription(updated.getRoleDescription());
        return rolesRepository.save(exist);
    }

    @Override
    public void delete(Integer id) {
        rolesRepository.deleteById(id);
    }

    @Override
    public Roles getById(Integer id) {
        return rolesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    @Override
    public List<Roles> getAll() {
        return rolesRepository.findAll();
    }
}
