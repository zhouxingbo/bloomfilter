/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.wxisme.bloomfilter.bitset;

import redis.clients.jedis.*;

import java.util.*;

/**
 * Implement bloom filter on redis bitset.
 */
public class RedisBitSet<E> implements BaseBitSet<E> {

    private JedisCluster jedisCluster;
    private JedisPool jedisPool;
    private String name;

    private boolean isCluster = true;

    private Pipeline pipeline;

    private Pipeline readPipeline;

    private Map<E,List<Response<Boolean>>> resultMap=new HashMap();

    private RedisBitSet() {
    }

    /**
     * Create a redis bitset.
     * @param jedisCluster jedis cluster client.
     * @param name the redis bit key name.
     */
    public RedisBitSet(JedisCluster jedisCluster, String name) {
        this.jedisCluster = jedisCluster;
        this.name = name;
        this.isCluster = true;
    }

    /**
     * Create a redis bitset.
     * @param jedisPool jedis client.
     * @param name the redis bit key name.
     */
    public RedisBitSet(JedisPool jedisPool, String name) {
        this.jedisPool = jedisPool;
        this.name = name;
        this.isCluster = false;
    }


    public void set(int bitIndex) {
        if (this.isCluster) {
            this.jedisCluster.setbit(this.name, bitIndex, true);
        } else {
            Jedis jedis=null;
            try {
                jedis = this.getJedis(jedisPool);
                jedis.setbit(this.name, bitIndex, true);
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                jedis.close();
            }

        }
    }

    public boolean get(E element,int bitIndex) {
        if (this.isCluster) {
            return this.jedisCluster.getbit(this.name, bitIndex);
        } else {
            List<Response<Boolean>> l=resultMap.get(element);
            if(l==null){
                l=new ArrayList<>();
            }
            
            l.add(this.readPipeline.getbit(this.name, bitIndex));
            resultMap.put(element,l);
            return true;
           /* Jedis jedis=null;
            try {
                jedis = this.getJedis(jedisPool);
                return jedis.getbit(this.name, bitIndex);
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }finally{
                jedis.close();
            }*/
        }
    }

    public void set(int bitIndex, boolean value) {
        if (this.isCluster) {
            this.jedisCluster.setbit(this.name, bitIndex, value);
        } else {
            this.pipeline.setbit(this.name, bitIndex, value);
            /*Jedis jedis=null;
            try {
                jedis = this.getJedis(jedisPool);
                jedis.setbit(this.name, bitIndex, value);
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                jedis.close();
            }*/
        }
    }

    public void clear(int bitIndex) {
        if (this.isCluster) {
            this.jedisCluster.setbit(this.name, bitIndex, false);
        } else {
            Jedis jedis=null;
            try {
                jedis = this.getJedis(jedisPool);
                jedis.setbit(this.name, bitIndex, false);
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                jedis.close();
            }
        }
    }

    public void clear() {
        if (this.isCluster) {
            this.jedisCluster.del(this.name);
        } else {
            Jedis jedis=null;
            try {
                jedis = this.getJedis(jedisPool);
                jedis.del(this.name);
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                jedis.close();
            }
        }
    }

    public long size() {
        if (this.isCluster) {
            return this.jedisCluster.bitcount(this.name);
        } else {
            Jedis jedis=null;
            try {
                jedis = this.getJedis(jedisPool);
                return jedis.bitcount(this.name);
            }catch(Exception e){
                e.printStackTrace();
                return 0;
            }finally{
                jedis.close();
            }
        }
    }

    public static synchronized Jedis getJedis(JedisPool pool) {
        Jedis jedis = null;
        try{
            if(pool != null) {
                jedis = pool.getResource();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return jedis;
    }

    public synchronized void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnResource(jedis);
        }
    }

    public synchronized void returnBrokenResource(Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnBrokenResource(jedis);
        }
    }

    public boolean isEmpty() {
        return size() <= 0;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public Pipeline getReadPipeline() {
        return readPipeline;
    }

    public void setReadPipeline(Pipeline readPipeline) {
        this.readPipeline = readPipeline;
    }

    public Map<E, List<Response<Boolean>>> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<E, List<Response<Boolean>>> resultMap) {
        this.resultMap = resultMap;
    }
}
