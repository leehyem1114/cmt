package com.example.cmtProject.repository.erp.saleMgt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.entity.mes.standardInfoMgt.Clients;

@Repository
public interface ClientsRepository extends JpaRepository<Clients, Long>{

}