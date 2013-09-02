package net.audumla.util;

import java.util.Date;


import net.audumla.collections.ExpiringMap;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.Test;

public class ExpiringCacheTest {
	@Test
	public void testExpirary() {
		Time.setNow(new Date());
		ExpiringMap ec = new ExpiringMap();
		ec.addExpirationRule("test.*", Time.offset(Time.getZeroDate(), 0, 0, 2));
		ec.add("default");
		ec.add("testCache");
		Assert.assertFalse(ec.hasDataExpired("testCache"));
		Assert.assertFalse(ec.hasDataExpired("default"));
		Time.setNow(Time.offset(Time.getNow(),0,0,3));
		Assert.assertTrue(ec.hasDataExpired("testCache"));
		Assert.assertFalse(ec.hasDataExpired("default"));
	}

	@Test
	public void testPropertyLoad() throws ConfigurationException {
		Time.setNow(new Date());
		ExpiringMap ec = new ExpiringMap(new PropertiesConfiguration("cache.properties"));
		ec.add("default");
		ec.add("testCache.json");
		Time.setNow(Time.offset(Time.getNow(),0,0,14));
		Assert.assertFalse(ec.hasDataExpired("testCache.json"));
		Assert.assertFalse(ec.hasDataExpired("default"));
		Time.setNow(Time.offset(Time.getNow(),0,0,16));
		Assert.assertTrue(ec.hasDataExpired("testCache.json"));
		Assert.assertFalse(ec.hasDataExpired("default"));
	}
}
