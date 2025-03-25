package com.example.cmtProject.repository.erp.saleMgt;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.cmtProject.entity.erp.salesMgt.SalesOrder;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
	
	//- 수주 목록에 있는 거래처코드 -
	@Query("""
			SELECT DISTINCT(s.cltCode) FROM SalesOrder s
			""")
	List<String> findByGetCltCode();
	
	//- 선택된 거래처명 가져오기 -
	@Query("SELECT c.cltName " +
	       "FROM Clients c " +
	       "WHERE c.cltCode = :cltCode")
     String findByGetCltName(@Param("cltCode") String cltCode);
	
	//- 수주 목록에 있는 제품코드 -
	@Query("""
			SELECT DISTINCT(s.pdtCode)from SalesOrder s order by s.pdtCode
			""")
	List<String> findByGetPdtCode();
	
	//- 수주 목록에 있는 제품명 -
	@Query("""
			SELECT p.pdtName
			FROM Products p
			WHERE p.pdtCode = :pdtCode
			""")
	String findByGetPdtName(@Param("pdtCode") String pdtCode);
}

/*
 * Clients는 엔티티명으로 앞에 대문자
 * c.cltName 객체로 접근해야 하므로 cltName이 아니라 c.cltName
 * = :변수명 -> = 와 : 는 띄운다
 * 
 */
