package jp.tannakaken.infinitenion.operand;

import java.math.BigInteger;

import jp.tannakaken.infinitenion.calculator.CalculatingException;

/**
 * {@link Operand}�����[����ϐ��B<br>
 * ���̃N���X�̃C���X�^���X�����[���Ȃ��悤�ɒ��ӁB�������[�v���N����\��������B<br>
 * �����o�[�Ƃ���{@link Operand}�̃C���X�^���X{@link Variable#mInterior}��������A
 *�@substitution���Ȃ킿����ȊO�̉��Z�q�Ɋւ��ẮA����{@link Variable#mInterior}�ɈϏ�����B<br>
 * ����{@link Operand}�Ɠ��ꎋ���Ȃ���A���낢��Ȓl���Ƃ肤��@�\���A�Ϗ���Decorator�p�^�[�����g���Ď����B
 * @author tannakaken�B
 *
 * @see
 * <a href="http://en.wikipedia.org/wiki/Decorator_pattern">http://en.wikipedia.org/wiki/Decorator_pattern</a><br>
 * <a href="http://en.wikipedia.org/wiki/Delegation_(programming)">
 * http://en.wikipedia.org/wiki/Delegation_(programming)</a>
 *
 */
class Variable implements Operand {
	
	/**
	 * �ϐ��̒��g�B�����l��0�B
	 */
	private Constant mInterior = Zero.ZERO;
	/**
	 * �v�Z���L�����Z�����ꂽ���̂��߂ɁA�ȑO�̏�Ԃ�ۑ����Ă����B
	 */
	private Constant mFormerInterior;
	
	@Override
	public void setInterior(final Constant aInterior) {
		if (mFormerInterior == null) {
			mFormerInterior = mInterior;
		}
		mInterior = aInterior;
	}
	@Override
	public Constant getInterior() {
		return mInterior;
	}
	/**
	 * ������L�����Z������B
	 */
	public void cancel() {
		if (mFormerInterior != null) {
			mInterior = mFormerInterior;
			mFormerInterior = null;
		}
	}
	/**
	 * ������m�肷��B
	 */
	public void settle() {
		mFormerInterior = null;
	}
	@Override
	public Constant getReal() {
		return mInterior.getReal();
	}

	@Override
	public Constant getImag() {
		return mInterior.getImag();
	}
	
	@Override
	public int getHeight() {
		return mInterior.getHeight();
	}
	
	@Override
	public Constant add(final Operand aOperand) {
		return mInterior.add(aOperand);
	}
	
	@Override
	public Constant mul(final Operand aOperand) {
		return mInterior.mul(aOperand);
	}
	
	@Override
	public Constant negate() {
		return mInterior.negate();
	}
	@Override
	public Constant div(final Operand aOperand) {
		return mInterior.div(aOperand);
	}
	@Override
	public Constant inv() throws CalculatingException {
		return mInterior.inv();
	}
	
	@Override
	public boolean isZero() {
		return mInterior.isZero();
	}
	
	@Override
	public boolean isInteger() {
		return mInterior.isInteger();
	}

	@Override
	public BigInteger getInteger() {
		return mInterior.getInteger();
	}
	/**
	 * @return {@link Variable#mInterior}�̕�����\��
	 */
	@Override
	public String toString() {
		return mInterior.toString();
	}
}
