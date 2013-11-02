package com.htm.test;

import com.htm.peopleresolution.ILPGResolutionProviderFactory;
import com.htm.peopleresolutionprovider.LpgResolutionProvider_UserByGroup;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created with IntelliJ IDEA.
 * User: ungerts
 * Date: 01.11.13
 * Time: 19:47
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/spring-beans.xml")
@Ignore
public class LPGFactoryTest {

    private static final Logger log = LoggerFactory.getLogger(LPGFactoryTest.class);

   @Autowired
    ILPGResolutionProviderFactory ilpgResolutionProviderFactory;

    @Test
    public void defaultProvider() {

        log.debug("Resolution provider class: " + this.ilpgResolutionProviderFactory.createPeopleResolutionProvider(null).getClass());
        Assert.assertTrue(false);
    }
}
