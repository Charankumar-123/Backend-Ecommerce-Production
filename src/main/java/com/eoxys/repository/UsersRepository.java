package com.eoxys.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.eoxys.entity.UsersEntity;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity, Long> {
	boolean existsByEmail(String email);
	boolean existsById(Long userId);
//	UsersEntity findByUsername(String email);
//	UsersEntity findByEmail(String email);
	Optional<UsersEntity> findByEmail(String email); 

	

}
