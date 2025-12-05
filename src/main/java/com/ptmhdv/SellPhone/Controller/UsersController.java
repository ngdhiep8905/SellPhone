package com.ptmhdv.SellPhone.Controller;

import com.ptmhdv.SellPhone.Entity.Users;
import com.ptmhdv.SellPhone.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UsersController {

    @Autowired
    private UsersRepository usersRepo;

    /* =======================================================
       1) LẤY TẤT CẢ NGƯỜI DÙNG
    ======================================================== */
    @GetMapping
    public List<Users> getAllUsers() {
        return usersRepo.findAll();
    }

    /* =======================================================
       2) LẤY 1 NGƯỜI DÙNG THEO ID
    ======================================================== */
    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable String id) {
        return usersRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /* =======================================================
       3) CẬP NHẬT TRẠNG THÁI USER (ACTIVE / LOCKED)
       Endpoint: PUT /api/users/{id}/status
    ======================================================== */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {

        return usersRepo.findById(id).map(user -> {

            String newStatus = body.get("status");
            user.setStatus(newStatus);
            usersRepo.save(user);

            return ResponseEntity.ok().build();

        }).orElse(ResponseEntity.notFound().build());
    }

    /* =======================================================
       4) TẠO NGƯỜI DÙNG MỚI (DÙNG CHO USER ĐĂNG KÝ)
       NOT USED BY ADMIN (but good to have)
    ======================================================== */
    @PostMapping
    public ResponseEntity<Users> createUser(@RequestBody Users u) {
        if (usersRepo.existsByEmail(u.getEmail())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(usersRepo.save(u));
    }

    /* =======================================================
       5) XÓA NGƯỜI DÙNG (ADMIN)
    ======================================================== */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        if (!usersRepo.existsById(id)) return ResponseEntity.notFound().build();
        usersRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping
    public ResponseEntity<?> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Users> result;

        if (search != null && !search.isEmpty()) {
            result = usersRepo.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    search, search, pageable
            );
        } else {
            result = usersRepo.findAll(pageable);
        }

        return ResponseEntity.ok(result);
    }

}
