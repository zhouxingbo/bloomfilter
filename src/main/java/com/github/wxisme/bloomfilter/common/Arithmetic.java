package com.github.wxisme.bloomfilter.common;

import java.util.*;

public class Arithmetic {

	// 放大倍数
	private static final int mulriple = 100;

	public int pay(List<Prize> prizes) {
		int lastScope = 0;
		// 洗牌，打乱奖品次序
		Collections.shuffle(prizes);
		Map<Integer, int[]> prizeScopes = new HashMap<Integer, int[]>();
		Map<Integer, Integer> prizeQuantity = new HashMap<Integer, Integer>();
		for (Prize prize : prizes) {
			int prizeId = prize.getPrizeId();
			// 划分区间
			int currentScope = lastScope + prize.getProbability();
			prizeScopes.put(prizeId, new int[]{lastScope + 1, currentScope});
			prizeQuantity.put(prizeId, prize.getQuantity());

			lastScope = currentScope;
		}


		// 获取1-1000000之间的一个随机数
		int luckyNumber = new Random().nextInt(mulriple);
		int luckyPrizeId = 0;
		// 查找随机数所在的区间
		if ((null != prizeScopes) && !prizeScopes.isEmpty()) {
			Set<Map.Entry<Integer, int[]>> entrySets = prizeScopes.entrySet();
			for (Map.Entry<Integer, int[]> m : entrySets) {
				int key = m.getKey();
				System.out.println("key " + key + ",value:" + m.getValue()[0] + "," +m.getValue()[1]);
				if (luckyNumber >= m.getValue()[0] && luckyNumber <= m.getValue()[1] && prizeQuantity.get(key) > 0) {
					luckyPrizeId = key;
					break;
				}
			}
		}

		if (luckyPrizeId > 0) {
		// 奖品库存减一
		}

		return luckyPrizeId;
	}

	//3. 测试
	//		prize1概率: 5%
	//
	//		prize2概率: 10%
	//
	//		prize3概率: 15%
	//
	//		prize4概率: 20%
	//
	//		prize5概率: 50%
	public static void main(String[] args) {
		List<Prize> prizes = new ArrayList<Prize>();
		Prize prize1 = new Prize();
		prize1.setPrizeId(1);
		prize1.setProbability(5);
		prize1.setQuantity(1);
		prizes.add(prize1);

		Prize prize2 = new Prize();
		prize2.setPrizeId(2);
		prize2.setProbability(10);
		prize2.setQuantity(10);
		prizes.add(prize2);

		Prize prize3 = new Prize();
		prize3.setPrizeId(3);
		prize3.setProbability(15);
		prize3.setQuantity(20);
		prizes.add(prize3);

		Prize prize4 = new Prize();
		prize4.setPrizeId(4);
		prize4.setProbability(20);
		prize4.setQuantity(50);
		prizes.add(prize4);

		Prize prize5 = new Prize();
		prize5.setPrizeId(5);
		prize5.setProbability(50);
		prize5.setQuantity(200);
		prizes.add(prize5);

		int prize1GetTimes = 0;
		int prize2GetTimes = 0;
		int prize3GetTimes = 0;
		int prize4GetTimes = 0;
		int prize5GetTimes = 0;
		Arithmetic arithmetic = new Arithmetic();
		int times = 1000;
		for (int i = 0; i < times; i++) {
			synchronized(String.class){
				int prizeId = arithmetic.pay(prizes);
				switch (prizeId) {
					case 1:
						prize1GetTimes++;
						break;
					case 2:
						prize2GetTimes++;
						break;
					case 3:
						prize3GetTimes++;
						break;
					case 4:
						prize4GetTimes++;
						break;
					case 5:
						prize5GetTimes++;
						break;
				}
			}

		}
		System.out.println("抽奖次数" + times);
		System.out.println("prize1中奖次数" + prize1GetTimes);
		System.out.println("prize2中奖次数" + prize2GetTimes);
		System.out.println("prize3中奖次数" + prize3GetTimes);
		System.out.println("prize4中奖次数" + prize4GetTimes);
		System.out.println("prize5中奖次数" + prize5GetTimes);
	}
}

class Prize {

	/**
	 * 奖品唯一标示
	 */
	private Integer prizeId;

	/**
	 * 中奖概率
	 */
	private Integer probability;

	/**
	 * 奖品数量
	 */
	private Integer quantity;

	public Integer getPrizeId() {
		return prizeId;
	}

	public void setPrizeId(Integer prizeId) {
		this.prizeId = prizeId;
	}

	public Integer getProbability() {
		return probability;
	}

	public void setProbability(Integer probability) {
		this.probability = probability;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}