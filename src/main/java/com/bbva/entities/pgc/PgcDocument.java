package com.bbva.entities.pgc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "pgc_documents")
public class PgcDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "sdatool", nullable = false)
    private String sdatool;

    @Column(name = "domain_name")
    private String domainName;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "uploading_user", nullable = false)
    private String uploadingUser;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "uploaded_at", insertable = false, updatable = false)
    private Date uploadedAt;

    @Column(name = "q_registro")
    private String qRegistro;

    @Column(name = "modification_date", insertable = false, updatable = false)
    private Date modificationDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
