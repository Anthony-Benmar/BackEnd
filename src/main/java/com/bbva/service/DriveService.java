package com.bbva.service;

import com.bbva.common.googleDrive.GoogleDriveConfig;
import com.bbva.dao.DriveDao;
import com.bbva.dto.drive.FolderDto;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class DriveService {

    private final SqlSessionFactory sqlSessionFactory;

    public DriveService(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public FolderDto crearCarpeta(String nombre) throws Exception {
        Drive driveService = GoogleDriveConfig.getDriveService();

        File metadata = new File();
        metadata.setName(nombre);
        metadata.setMimeType("application/vnd.google-apps.folder");

        File carpeta = driveService.files()
                .create(metadata)
                .setFields("id, name")
                .execute();

        FolderDto dto = new FolderDto(carpeta.getId(), carpeta.getName());

        try (SqlSession session = sqlSessionFactory.openSession()) {
            DriveDao dao = session.getMapper(DriveDao.class);
            dao.insertarCarpeta(dto);
            session.commit();
        }

        return dto;
    }
}
