package com.example.demo.businessPost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessPostRepository extends JpaRepository<BusinessPosts, Integer>{

}
