package com.htm.test;

import com.htm.ITaskClientInterface;
import com.htm.db.spring.DataAccessRepositoryCustom;
import com.htm.db.spring.DataAccessRepositoryImpl;
import com.htm.exceptions.DatabaseException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: ungerts
 * Date: 31.10.13
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/spring-beans.xml")
@Ignore
public class DatabaseTest {

    final Logger logger = LoggerFactory.getLogger(DatabaseTest.class);

    @Autowired
    private DataAccessRepositoryCustom dataAccessRepository;

    @Autowired
    private ITaskClientInterface taskClientInterface;

    @Test
    public void checkDatabase(){
        try {

            Assert.assertNotNull(dataAccessRepository.getTaskInstance("123"));
        } catch (DatabaseException e) {
            logger.debug(e.getMessage(),e);
            Assert.fail(e.getMessage());
        }
    }
}
