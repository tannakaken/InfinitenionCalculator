package jp.tannakaken.infinitenion.operand;

import java.math.BigInteger;

/**
 * {@link Constant}を{@link Object#toString}することを、Visitorパターンを使って実現する。
 * toStringのモードを一括管理するために、singletonパターンも使う。
 * @author kensaku
 * 
 * @see 
 * <a href="http://en.wikipedia.org/wiki/Visitor_pattern">http://en.wikipedia.org/wiki/Visitor_pattern</a><br>
 *　<a href="http://en.wikipedia.org/wiki/Singleton_pattern">http://en.wikipedia.org/wiki/Singleton_pattern</a>
 */
final class ConstantStringConverter {
	/**
	 * 今、いくつめの基底を文字にしているか。
	 */
	private BigInteger mNthBase = BigInteger.ZERO;
	/**
	 * いくつめの元を作っているか。
	 */
	private int mCount = 0;
	/**
	 * {@link Constant#toString}の結果を集めていく。
	 */
	private StringBuilder mResult = new StringBuilder();
	/**
	 * {@link ConstantStringConverter}の<a href="http://en.wikipedia.org/wiki/Singleton_pattern">singleton</a>。
	 * 
	 */
	private static ConstantStringConverter mSingleton = new ConstantStringConverter();
	/**
	 * 外からインスタンス化させない。
	 */
	private ConstantStringConverter() { }
	/**
	 * 
	 * @return {@link ConstantStringConverter}の<a href="http://en.wikipedia.org/wiki/Singleton_pattern">singleton</a>
	 * 
	 */
	public static ConstantStringConverter getInstance() {
		return mSingleton;
	}
	/**
	 * 
	 * @param aConstant {@link Constant#toString}される{@link Constant}。
	 * @return toStringの結果。
	 */
	public String convertString(final Constant aConstant) {
		visit(aConstant);
		String tResult = getResult();
		clear();
		return tResult;
	}
	/**
	 * 
	 * @param aConstant <a href="http://en.wikipedia.org/wiki/Visitor_pattern">Visitor</a>パターンで
	 * 要素を{@link Constant#toString}していく。
	 * 
	 */
	private void visit(final Constant aConstant) {
		if (aConstant.getHeight() == 0) {
			if (!aConstant.isZero()) {
				if (aConstant.isOne()) {
					if (mNthBase.equals(BigInteger.ZERO)) {
						mResult.append(aConstant.toString());
					} else {
						mResult.append(" " + sign(mNthBase));
					}
				} else {
					mResult.append(" " + aConstant.toString());
					if (mNthBase.compareTo(BigInteger.ZERO) > 0) {
						mResult.append(" " + sign(mNthBase) + " *");
					}
				}
				mCount++;
			}
			mNthBase = mNthBase.add(BigInteger.ONE);
		} else {
			this.visit(aConstant.getReal());
			mNthBase = mNthBase.add(omittedBases(aConstant.getHeight(), aConstant.getReal().getHeight()));
			this.visit(aConstant.getImag());
			mNthBase = mNthBase.add(omittedBases(aConstant.getHeight(), aConstant.getImag().getHeight()));
		}
	}
	/**
	 * 
	 * @return {@link Constant#toString}の結果を集めたもの。。
	 */
	private String getResult() {
		StringBuilder tSuffix = new StringBuilder();
		for (int i = 1; i < mCount; i++) {
			tSuffix.append(" +");
		}
		return mResult.toString().trim() + tSuffix.toString();
	}
	/**
	 * 初期化。
	 */
	private void clear() {
		mNthBase = BigInteger.ZERO;
		mCount = 0;
		mResult.setLength(0);
	}
	/**
	 * @param aCounter いくつめの基底か。
	 * @return 記号。
	 */
	private String sign(final BigInteger aCounter) {
		return "E" + aCounter;
	}
	/**
	 * 
	 * @param aParentHeight 親の高さ。
	 * @param aChildHeight 子の高さ。
	 * @return 省略された基底の数。
	 */
	private BigInteger omittedBases(final int aParentHeight, final int aChildHeight) {
		if (aParentHeight - aChildHeight == 1) {
			return BigInteger.ZERO;
		} else {
			// 2^aCHildHeight * (2^(aParentHeight - aChildHeight - 1) - 1) 
			return BigInteger.valueOf(2).pow(aChildHeight)
					.multiply(Constant.TWO.pow(aParentHeight - aChildHeight - 1)
							.subtract(BigInteger.ONE));
		}
	}
	
}
