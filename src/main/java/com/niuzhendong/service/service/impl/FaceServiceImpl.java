package com.niuzhendong.service.service.impl;

import com.arcsoft.face.ImageQuality;
import com.niuzhendong.service.service.FaceService;
import com.niuzhendong.service.utils.FaceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FaceServiceImpl implements FaceService {

    @Autowired
    private FaceUtils faceUtils;

    private ImageQuality imageQualityDetect


}
