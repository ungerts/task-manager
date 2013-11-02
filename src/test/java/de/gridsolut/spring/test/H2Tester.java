package de.gridsolut.spring.test;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created with IntelliJ IDEA.
 * User: ungerts
 * Date: 31.10.13
 * Time: 18:11
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/test-context.xml")
@Ignore
public class H2Tester {

    @Test
    public void stupidTest() {
        Assert.assertTrue(true);
    }
}
