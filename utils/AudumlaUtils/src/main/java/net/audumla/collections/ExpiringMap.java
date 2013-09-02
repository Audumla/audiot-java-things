package net.audumla.collections;

import net.audumla.util.Time;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpiringMap {
	private Map<String, Date> cacheMap = new HashMap<String, Date>();
	private Map<Pattern, Date> expressionMap = new HashMap<Pattern, Date>();
	private Date defaultOffset = new Date(60 * 60 * 1000); // 1 hour

	public ExpiringMap() {

	}

	public ExpiringMap(PropertiesConfiguration props) {
		loadProperties(props);
	}

	public void setDefaultOffset(Date offset) {
		this.defaultOffset = offset;
	}

	@SuppressWarnings("unchecked")
    public void loadProperties(PropertiesConfiguration props) {
		for (Iterator<String> it = props.getKeys(); it.hasNext();) {
			String key = it.next();
			List<String> o = (List<String>) props.getProperty(key);
			if (o.size() == 3) {
				Date offsetTime = Time.offset(Time.getZeroDate(), Integer.parseInt(o.get(0)), Integer.parseInt(o.get(1)), Integer.parseInt(o.get(2)));
				addExpirationRule(key, offsetTime);
			}
		}

	}

	public void addExpirationRule(String expression, Date offset) {
		expressionMap.put(Pattern.compile(expression), offset);
	}

	public boolean hasDataExpired(String key) {
		Date expires = cacheMap.get(key);
		return expires == null || expires.before(Time.getNow());
	}

	private Date getExpiresDate(String key, Date now) {
		for (Pattern p : expressionMap.keySet()) {
			Matcher m = p.matcher(key);
			if (m.matches()) {
				return Time.offset(now, expressionMap.get(p));
			}
		}
		return Time.offset(now, defaultOffset);
	}

	public void add(String key) {
		cacheMap.put(key, getExpiresDate(key, Time.getNow()));
	}

	public void remove(String key) {
		cacheMap.remove(key);
	}

	public boolean contains(String key) {
		return cacheMap.containsKey(key);
	}
}
