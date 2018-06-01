package com.slerpio.teachme.model;

import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.slerpio.teachme.helper.CoreException;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Domain implements Map<String, Object>, Serializable{
    // Use Concurrent HashMap for thread safe
    private Map<String, Object> map = new LinkedHashMap<>();
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    static {
        jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        jsonMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        jsonMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        jsonMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }
    static public final Domain instances = new Domain();
    public Domain() {
    }

    public Domain(Object object) {
        this(writeTo(object));
    }

    public Domain(Domain other) {
        this(other.toString());
    }

    @SuppressWarnings("unchecked")
    public Domain(String other) {
        try {
            this.map = (Map<String, Object>) jsonMapper.readValue(other, HashMap.class);
        } catch (JsonParseException e) {
            Log.d("should json >>>", other);
            throw new CoreException("failed.to.parse.json", e);
        } catch (JsonMappingException e) {
            throw new CoreException("failed.to.mapping.json", e);
        } catch (IOException e) {
            throw new CoreException("failed.to.read.json.data", e);
        }
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public Object get(Object key) {
        return map.get(key);
    }

    public Boolean getBoolean(String key) {
        return Boolean.valueOf(getString(key));
    }

    public Long getLong(String key) {
        return Long.valueOf(getString(key));
    }

    public Integer getInt(String key) {
        return Integer.valueOf(getString(key));
    }

    public Short getShort(String key) {
        return Short.valueOf(getString(key));
    }

    public Double getDouble(String key) {
        return Double.valueOf(getString(key));
    }

    public Float getFloat(String key) {
        return Float.valueOf(getString(key));
    }

    public BigInteger getBigInt(String key) {
        return BigInteger.valueOf(getLong(key));
    }

    public BigDecimal getBigDecimal(String key) {
        return BigDecimal.valueOf(getDouble(key));
    }

    public String getString(String key) {
        if(map.containsKey(key))
            return String.valueOf(map.get(key));
        return null;
    }

    public Object putIfAbsent(String key, Object value) {
        return map.putIfAbsent(key, value);
    }

    public Domain put(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public Object remove(Object key) {
        return map.remove(key);
    }

    public void putAll(Map<? extends String, ? extends Object> m) {
        map.putAll(m);
    }

    public void clear() {
        map.clear();
    }
    @Override
    public Set<String> keySet() {
        return map.keySet();
    }
    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return this.map.entrySet();
    }

    public List<Domain> getList(String key) {
        try {
            String result = jsonMapper.writeValueAsString(this.map.get(key));
            List<Domain> dtoList = jsonMapper.readValue(result, new TypeReference<List<Domain>>() {
            });
            return dtoList;
        } catch (JsonProcessingException e) {
            throw new CoreException("the.value.should.be.json.list." + key, e);
        } catch (IOException e) {
            throw new CoreException("failed.to.read.json.data", e);
        }
    }
    public List<Map<Object, Object>> getListAsMap(String key) {
        try {
            String result = jsonMapper.writeValueAsString(this.map.get(key));
            List<Map<Object, Object>> dtoList = jsonMapper.readValue(result, new TypeReference<List<Map<Object, Object>>>() {
            });
            return dtoList;
        } catch (JsonProcessingException e) {
            throw new CoreException("the.value.should.be.json.list." + key, e);
        } catch (IOException e) {
            throw new CoreException("failed.to.read.json.data", e);
        }
    }

    public List<String> getListString(String key) {
        try {
            String result = jsonMapper.writeValueAsString(this.map.get(key));
            List<String> dtoList = jsonMapper.readValue(result, new TypeReference<List<String>>() {});
            return dtoList;
        } catch (JsonProcessingException e) {
            throw new CoreException("the.value.should.be.json.list." + key, e);
        } catch (IOException e) {
            throw new CoreException("failed.to.read.json.data", e);
        }
    }
    public Set<Domain> getSet(String key) {
        try {
            String result = jsonMapper.writeValueAsString(this.map.get(key));
            Set<Domain> dtoList = jsonMapper.readValue(result, new TypeReference<Set<Domain>>() {
            });
            return dtoList;
        } catch (JsonProcessingException e) {
            throw new CoreException("the.value.should.be.json.set." + key, e);
        } catch (IOException e) {
            throw new CoreException("failed.to.read.json.data", e);
        }
    }

    public Domain getDomain(String key) {
        try {
            String result = jsonMapper.writeValueAsString(this.map.get(key));
            Domain dto = jsonMapper.readValue(result.getBytes(), Domain.class);
            return dto;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new CoreException("the.value.should.be.json.object." + key, e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CoreException("failed.to.read.json.data", e);
        }
    }

    public <T> T convertTo(Class<T> classToSerialize) {
        try {
            return jsonMapper.readValue(toString(), classToSerialize);
        } catch (IOException e) {
            throw new CoreException("failed.to.convert.object.from.json", e);
        }
    }

    public static String writeTo(Object object) {
        try {
            String result = jsonMapper.writeValueAsString(object);
            return result;
        } catch (JsonProcessingException e) {
            throw new CoreException(e);
        }
    }

    public String toString() {
        try {
            String result = jsonMapper.writeValueAsString(this.map);
            return result;
        } catch (JsonProcessingException e) {
            throw new CoreException(e);
        }
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public Bundle toBundle(){
        Bundle bundle = new Bundle();
        for (Entry<String, Object> entry : map.entrySet()){
            Object value = entry.getValue();
            String key = entry.getKey();
            if(value.getClass() == Integer.class || value.getClass() == int.class){
                bundle.putInt(key, (int)value);
            }else if(value.getClass() == Float.class || value.getClass() == float.class){
                bundle.putFloat(key, (float)value);
            }else if(value.getClass() == Short.class || value.getClass() == short.class){
                bundle.putShort(key, (short)value);
            }else if(value.getClass() == Double.class || value.getClass() == double.class){
                bundle.putDouble(key, (double)value);
            }else if(value.getClass() == Long.class || value.getClass() == long.class){
                bundle.putLong(key, (long)value);
            }else if(value.getClass() == Character.class || value.getClass() == char.class){
                bundle.putChar(key, (char)value);
            }else {
                bundle.putString(key, String.valueOf(value));
            }
        }
        return bundle;
    }
}
