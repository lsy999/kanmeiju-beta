package com.mrpi.kanmeiju.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig meijuDaoConfig;

    private final MeijuDao meijuDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        meijuDaoConfig = daoConfigMap.get(MeijuDao.class).clone();
        meijuDaoConfig.initIdentityScope(type);

        meijuDao = new MeijuDao(meijuDaoConfig, this);

        registerDao(Meiju.class, meijuDao);
    }
    
    public void clear() {
        meijuDaoConfig.getIdentityScope().clear();
    }

    public MeijuDao getMeijuDao() {
        return meijuDao;
    }

}
