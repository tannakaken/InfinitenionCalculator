package jp.tannakaken.infinitenion.operand;

import java.math.BigInteger;

/**
 * {@link Constant}��{@link Object#toString}���邱�Ƃ��AVisitor�p�^�[�����g���Ď�������B
 * toString�̃��[�h���ꊇ�Ǘ����邽�߂ɁAsingleton�p�^�[�����g���B
 * @author kensaku
 * 
 * @see 
 * <a href="http://en.wikipedia.org/wiki/Visitor_pattern">http://en.wikipedia.org/wiki/Visitor_pattern</a><br>
 *�@<a href="http://en.wikipedia.org/wiki/Singleton_pattern">http://en.wikipedia.org/wiki/Singleton_pattern</a>
 */
final class ConstantStringConverter {
	/**
	 * ���A�����߂̊��𕶎��ɂ��Ă��邩�B
	 */
	private BigInteger mNthBase = BigInteger.ZERO;
	/**
	 * �����߂̌�������Ă��邩�B
	 */
	private int mCount = 0;
	/**
	 * {@link Constant#toString}�̌��ʂ��W�߂Ă����B
	 */
	private StringBuilder mResult = new StringBuilder();
	/**
	 * {@link ConstantStringConverter}��<a href="http://en.wikipedia.org/wiki/Singleton_pattern">singleton</a>�B
	 * 
	 */
	private static ConstantStringConverter mSingleton = new ConstantStringConverter();
	/**
	 * �O����C���X�^���X�������Ȃ��B
	 */
	private ConstantStringConverter() { }
	/**
	 * 
	 * @return {@link ConstantStringCOnverter}��<a href="http://en.wikipedia.org/wiki/Singleton_pattern">singleton</a>
	 * 
	 */
	public static ConstantStringConverter getInstance() {
		return mSingleton;
	}
	/**
	 * 
	 * @param aConstant {@link Constant#toString}�����{@link Constant}�B
	 * @return toString�̌��ʁB
	 */
	public String convertString(final Constant aConstant) {
		visit(aConstant);
		String tResult = getResult();
		clear();
		return tResult;
	}
	/**
	 * 
	 * @param aConstant <a href="http://en.wikipedia.org/wiki/Visitor_pattern">Visitor</a>�p�^�[����
	 * �v�f��{@link Constant#toString}���Ă����B
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
	 * @return {@link Constant#toString}�̌��ʂ��W�߂����́B�B
	 */
	private String getResult() {
		StringBuilder tSuffix = new StringBuilder();
		for (int i = 1; i < mCount; i++) {
			tSuffix.append(" +");
		}
		return mResult.toString().trim() + tSuffix.toString();
	}
	/**
	 * �������B
	 */
	private void clear() {
		mNthBase = BigInteger.ZERO;
		mCount = 0;
		mResult.setLength(0);
	}
	/**
	 * @param aCounter �����߂̊�ꂩ�B
	 * @return �L���B
	 */
	private String sign(final BigInteger aCounter) {
		return "E" + aCounter;
	}
	/**
	 * 
	 * @param aParentHeight �e�̍����B
	 * @param aChildHeight �q�̍����B
	 * @return �ȗ����ꂽ���̐��B
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
