/** 
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva
 * and is protected by copyright law and international
 * treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties
 * and will be prosecuted to the maximum extent possible
 * under the law. Comviva reserves all rights not
 * expressly granted. You may not reverse engineer, decompile,
 * or disassemble the software, except and only to the
 * extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/

package com.btsl.user.businesslogic;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * The Class RedisUtil.
 *
 * @param <T>
 *            the generic type
 */
@Configuration
public class RedisUtil<T> {

    /** The redis template. */
    private RedisTemplate<String, T> redisTemplate;

    /** The hash operation. */
    private HashOperations<String, Object, T> hashOperation;

    /** The value operations. */
    private ValueOperations<String, T> valueOperations;

    /**
     * Instantiates a new redis util.
     *
     * @param redisTemplate
     *            the redis template
     */
    @Autowired
    RedisUtil(RedisTemplate<String, T> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperation = redisTemplate.opsForHash();
        this.valueOperations = redisTemplate.opsForValue();
    }

    /**
     * Put map.
     *
     * @param redisKey
     *            the redis key
     * @param key
     *            the key
     * @param data
     *            the data
     */
    public void putMap(String redisKey, Object key, T data) {
        hashOperation.put(redisKey, key, data);
    }

    /**
     * Gets the map as single entry.
     *
     * @param redisKey
     *            the redis key
     * @param key
     *            the key
     * @return the map as single entry
     */
    public T getMapAsSingleEntry(String redisKey, Object key) {
        return hashOperation.get(redisKey, key);
    }

    /**
     * Gets the map as all.
     *
     * @param redisKey
     *            the redis key
     * @return the map as all
     */
    public Map<Object, T> getMapAsAll(String redisKey) {
        return hashOperation.entries(redisKey);
    }

    /**
     * Put value.
     *
     * @param key
     *            the key
     * @param value
     *            the value
     */
    public void putValue(String key, T value) {
        valueOperations.set(key, value);
    }

    /**
     * Put value with expire time.
     *
     * @param key
     *            the key
     * @param value
     *            the value
     * @param timeout
     *            the timeout
     * @param unit
     *            the unit
     */
    public void putValueWithExpireTime(String key, T value, long timeout, TimeUnit unit) {
        valueOperations.set(key, value, timeout, unit);
    }

    /**
     * Gets the value.
     *
     * @param key
     *            the key
     * @return the value
     */
    public T getValue(String key) {
        return valueOperations.get(key);
    }

    /**
     * Sets the expire.
     *
     * @param key
     *            the key
     * @param timeout
     *            the timeout
     * @param unit
     *            the unit
     */
    public void setExpire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

    /**
     * Delete hash values.
     *
     * @param key
     *            the key
     * @param hashKeys
     *            the hash keys
     */
    public void deleteHashValues(String key, Object[] hashKeys) {
        hashOperation.delete(key, hashKeys);
    }

}
