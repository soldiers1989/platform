package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualProductAttachDao;
import com.fulu.game.core.entity.VirtualProductAttach;
import com.fulu.game.core.service.VirtualProductAttachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class VirtualProductAttachServiceImpl extends AbsCommonService<VirtualProductAttach, Integer> implements VirtualProductAttachService {

    @Autowired
    private VirtualProductAttachDao virtualProductAttachDao;


    @Override
    public ICommonDao<VirtualProductAttach, Integer> getDao() {
        return virtualProductAttachDao;
    }

}