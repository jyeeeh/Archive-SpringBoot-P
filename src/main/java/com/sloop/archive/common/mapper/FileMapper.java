package com.sloop.archive.common.mapper;

import com.sloop.archive.common.fileUtils.FileDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileMapper {
    void saveFiles(List<FileDTO> files);

    void updateFiles(List<FileDTO> files);
}
