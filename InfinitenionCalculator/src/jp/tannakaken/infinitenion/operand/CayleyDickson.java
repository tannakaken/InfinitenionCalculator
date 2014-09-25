package jp.tannakaken.infinitenion.operand;

import java.math.BigInteger;
/**
 * Cayley-Dickson�̍\���@�ɂ��A���������̒����f������������N���X�B<br>
 * �����Ƌ������\�����I�ɑ����񕪖؍\�����������A�}�ł��邱�̃N���X�ƁA�t�ł���{@link Rational}���X�[�p�[�N���X{@link Constant}�Ƃ��ē��ꎋ���邽�߂ɁA
 * Composite�p�^�[�����g�p���Ă���B<br>
 * �ڂ����́Apackage-info���Q�ƁB<br>
 * �{����Java�̎v�z���猾���΁A���̃N���X��immutable�ɂ���ׂ������AAndroid��œ��������߂ɂ́A
 * �Z���ȃI�u�W�F�N�g������������ƁA������������Ȃ��Ȃ�A�������m�ۂ̂��߂�darvik�̃K�x�[�W�R���N�^��GC_FOR_ALLOC���ʔ��������A
 * �A�v�����~�܂��Ă��܂��B<br>
 * ���ہA�����immutable�ɂ����݌v�ł��A�f�X�N�g�b�v�A�v���Ȃ�A�\���ȃX�s�[�h�œ������Aandroid���ƁAE11111111��10����x�ł��~�܂��Ă��܂��B<br>
 * ����āA���̃N���X��mutable�ɂ��Ă���A���Z������ƁAoperand�ɔj��I�ȕ���p���N����B<br>
 * �ǂ�operand�ɔj��I����p���N���邩�͂킩��Ȃ��̂ŁA���Z�Ɏg����Object�͍ė��p���Ă͂����Ȃ��B<br>
 * ������x�A�g�p���Ȃ��Ă͂����Ȃ��Ƃ��́A{@link Constant#copy()}���s���B
 * 
 * @author tannakaken
 *
 * @see 
 * <a href="http://en.wikipedia.org/wiki/Cayley%E2%80%93Dickson_construction">
 * http://en.wikipedia.org/wiki/Cayley%E2%80%93Dickson_construction</a><br>
 * <a href="http://en.wikipedia.org/wiki/Composite_pattern">http://en.wikipedia.org/wiki/Composite_pattern</a>
 *
 */
class CayleyDickson extends Constant {

	/**
	 * �����B�ȃ��������̂��߂�final�ɂ͂��Ȃ��B
	 */
	private final Constant mReal;
	/**
	 * �����B�ȃ��������̂��߂�final�ɂ͂��Ȃ��B
	 */
	private final Constant mImag;
	/**
	 * �����B���ꂪint�̌��E2147483647�𒴂��邱�Ƃ͂Ȃ��ƐM���闝�R������B<br>
	 * ���̂悤�ȍ������������P�ʂ̓Y������2^2147483647�ł���A���̂悤�Ȑ�����ł����ނɂ͉F���̎����ł��܂�����Ȃ�����ł���B
	 */
	private final int mHeight;
	/**
	 * ���̃N���X��{@link Object#toString}���邽�߂̃N���X�BVisitor�p�^�[�����g�p�B<br>
	 * �����������Singleton�Ȃ̂ŁA���̏ꏊ�ł��̃C���X�^���X��{@link ConstantStringConverter#setLimited(boolean)}����ƁA
	 * ���̃C���X�^���X�ɂ��e������B
	 * 
	 * @see 
	 * <a href="http://en.wikipedia.org/wiki/Visitor_pattern">http://en.wikipedia.org/wiki/Visitor_pattern</a><br>
	 * <a href="http://en.wikipedia.org/wiki/Singleton_pattern">http://en.wikipedia.org/wiki/Singleton_pattern</a>
	 * 
	 */
	private ConstantStringConverter mStringConverter = ConstantStringConverter.getInstance();
	
	/**
	 * 
	 * @param aReal �����B
	 * @param aImag �����B
	 * @param aHeight �����B
	 */
	CayleyDickson(final Constant aReal, final Constant aImag, final int aHeight) {
		if (aReal == null) {
			throw new IllegalArgumentException("������null�ł͂����܂���B");
		}
		if (aImag == null) {
			throw new IllegalArgumentException("������null�ł͂����܂���B");
		}
		if (aReal.getHeight() >= aHeight || aImag.getHeight() >= aHeight) {
			throw new IllegalArgumentException("�����Ƌ����̍����́A���̐��̍������Ⴍ�Ȃ��Ƃ����܂���B");
		}
		mReal = aReal;
		mImag = aImag;
		mHeight = aHeight;
	}

	@Override
	public final Constant add(final Operand aOperand) {
		throw new UnsupportedOperationException("�C���X�^���X���\�b�h��add�͍���0�̌��ɂ̂ݎ�������܂��B");
	}

	@Override
	public final Constant mul(final Operand aOperand) {
		throw new UnsupportedOperationException("�C���X�^���X���\�b�h��mul�͍���0�̌��ɂ̂ݎ�������܂��B");
	}

	@Override
	public final Constant negate() {
		throw new UnsupportedOperationException("�C���X�^���X���\�b�h��negate�͍���0�̌��ɂ̂ݎ�������܂��B");
	}

	@Override
	public final Constant div(final Operand aOperand) {
		throw new UnsupportedOperationException("�C���X�^���X���\�b�h��div�͍���0�̌��ɂ̂ݎ�������܂��B");
	}
	
	@Override
	public final Constant inv() {
		throw new UnsupportedOperationException("�C���X�^���X���\�b�h��inv�͍���0�̌��ɂ̂ݎ�������܂��B");
	}

	@Override
	public final boolean isZero() {
		return mReal.isZero() && mImag.isZero();
	}

	@Override
	final boolean isOne() {
		return mReal.isOne() && mImag.isZero(); 
	}
	

	@Override
	public final int getHeight() {
		return mHeight;
	}
	@Override
	public final Constant getReal() {
		return mReal;
	}
	@Override
	public final Constant getImag() {
		return mImag;
	}

	@Override
	final Constant drop() {
		if (mImag.isZero()) {
			return mReal;
		} else {
			return this;
		}
	}

	@Override
	public final String toString() {
		return mStringConverter.convertString(this);
	}

	@Override
	public final boolean isInteger() {
		return false;
	}

	@Override
	public final BigInteger getInteger() {
		throw new UnsupportedOperationException("���̃C���X�^���X�͐����ł͂���܂���B");
	}
	
	@Override
	public final boolean equals(final Object aOther) {
		if (this == aOther) {
			return true;
		}
		if (!(aOther instanceof CayleyDickson)) {
			return false;
		}
		CayleyDickson tOtherCayleyDickson = (CayleyDickson) aOther;
		return this.mHeight == tOtherCayleyDickson.mHeight
			&& this.mReal.equals(tOtherCayleyDickson.mReal)
			&& this.mImag.equals(tOtherCayleyDickson.mImag);
	}
	@Override
	public final int hashCode() {
		return mReal.hashCode() + mImag.hashCode() + mHeight;
	}

}
