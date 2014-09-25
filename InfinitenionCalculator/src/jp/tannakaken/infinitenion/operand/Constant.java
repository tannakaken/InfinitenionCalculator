package jp.tannakaken.infinitenion.operand;

import java.math.BigInteger;

import android.content.Context;
import jp.tannakaken.infinitenion.R;
import jp.tannakaken.infinitenion.calculator.CalculatingException;

/**
 * �萔���������ۃN���X�B<br>
 * ���̃N���X�́A�L������\��{@link Rational}�ƁA
 * Cayley-Dickson�̍\���@�ɂ��\�����ꂽ�����̋A�[�Ɍ��ł���L������̖��������㐔��\��{@link CayleyDickson}�̓�̋�ۃT�u�N���X�����B<br>
 * �����́A�����Ƌ����̓�̎}������CayleyDickson�Ǝ������������Ȃ��t�ł���Rational�ɂ��񕪖؂̍\���������Ă���B<br>
 * java�Ŗ؍\�����`�����΂ɂ��AComposite�p�^�[�����g�p���Ă���B
 * �܂����̃N���X��{@link Operand}�̎����ł�����B�B<br>
 * �܂��ACayley-Dickson�̍\���@�ɕK�v�ȃ��\�b�h�A
 * �����{@link ConstantStringConverter}�ɂ��{@link Object#toString}�ɕK�v�ȃ��\�b�h���S�Ē��ۃ��\�b�h�Ƃ��ėp�ӂ���Ă���B<br>
 * �܂�{@link Constant#drop()}�ɂ��A���ł��A���ʂȃ������̂Ȃ��œK�ȏ�Ԃɕۂ���Ă���B<br>
 * �Ȃ��Ȃ�{@link ImaginaryFactory}�Ő������ꂽ�Ƃ��͍œK�ŁA{@link Constant#calc(Operand[], String)}�̌v�Z�̍Ō��{@link Constant#drop()}
 * �ɂ��A�œK������Ă��邩��ł���B<br>
 * ��������������N���X�́A{@link Object#equals}��K�؂�override����ׂ��ł���B
 * @author tannakaken
 *
 * @see 
 * <a href="http://en.wikipedia.org/wiki/Template_method_pattern">
 * http://en.wikipedia.org/wiki/Template_method_pattern</a><br>
 * <a href="http://en.wikipedia.org/wiki/Composite_pattern">http://en.wikipedia.org/wiki/Composite_pattern</a><br>
 * <a href="http://en.wikipedia.org/wiki/Cayley%E2%80%93Dickson_construction">
 * http://en.wikipedia.org/wiki/Cayley%E2%80%93Dickson_construction</a>
 */
abstract class Constant implements Operand {

	/**
	 * {@link ImaginaryFactory}��{@link ConstantStringConverter}�ŁA<br>
	 * {@link CayleyDickson}���\��������A���������肷��Ƃ��Ɏg���萔�B
	 */
	static final BigInteger TWO = BigInteger.valueOf(2);

	/**
	 * 
	 * @return �ς̒P�ʌ����ǂ����B
	 */
	abstract boolean isOne();
	
	/**
	 * �l��0�ł���}���o���邾�����Ƃ������́B<br>
	 * �������[�̐ߖ�̂��߁B
	 * @return ���ʂȕ����𗎂Ƃ������́B
	 */
	abstract Constant drop();
	
	
	@Override
	public final void setInterior(final Constant aInterior) throws CalculatingException {
		throw new CalculatingException(R.string.substitution_not_variable_error);
	}
	@Override
	public final Constant getInterior() {
		return this;
	}
	/**
	 * @return {@link Constant}�𕶎���ɂ������́B<br/>
	 * {@link Rational}�Ȃ�΁A�t�|�[�����h�������\���ua b /�v<br/>
	 * {@link Real}�Ȃ�΁A�����\�L<br/>
	 * ��������{@link CayleyDickson}�Ȃ�΁A���N���X�ɋ����P��En���|�������̂̈ꎟ�������A
	 * �t�|�[�����h���ɕ\���������̂ł���B
	 */
	@Override
	public abstract String toString();
	
	/**
	 * �������x�̐ݒ�����o�����߂�{@link Context}�B
	 */
	private static Context mContext;
	/** 
	 * @param aContext ���݂�{@link Context}�B
	 */
	static void setContext(final Context aContext) {
		mContext = aContext;
	}
	/**
	 * 
	 * @return ���݂�{@link Context}
	 */
	static Context getContext() {
		return mContext;
	}
}
