package jp.tannakaken.infinitenion.operand;

import java.math.BigInteger;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import jp.tannakaken.infinitenion.R;
import jp.tannakaken.infinitenion.calculator.BackgroundProcessCancelledException;
import jp.tannakaken.infinitenion.calculator.CalculatingException;
/**
 * �X�^�b�N�ɗ��܂���{@link Operand}������āA�I�y���[�^�ɍ��킹�Čv�Z���A
 * �V����{@link Operand}���X�^�b�N�ɂށB<br>
 * ������C���X�^���X���K�v�Ȃ��̂ŁASingleton�p�^�[�����g���B<br>
 * �܂��A{@link Constant}��{@link Operand#getHeight()}�̒l��0�ŁA
 * {@link Zero}�̃C���X�^���X�łȂ��Ƃ��ɁA{@link Operand#add(Operand)}�A
 * {@link Operand#mul(Operand)}�A{@link Operand#negate()}�A{@link Operand#inv()}�A
 * ����������΁A�S�Ẳ��Z�����������̂́A����Template Method�p�^�[���ł���B
 * @author tannakaken
 * 
 * @see
 * <a href="http://en.wikipedia.org/wiki/Singleton_pattern">http://en.wikipedia.org/wiki/Singleton_pattern</a><br>
 * <a href="http://en.wikipedia.org/wiki/Template_method_pattern">
 * http://en.wikipedia.org/wiki/Template_method_pattern</a>
 */
public final class ResultantFactory extends Factory {
	/**
	 * {@link Constant}����邽�߁B 
	 */
	private static ImaginaryFactory mImaginaryFactory = ImaginaryFactory.getInstance();
	
	/**
	 * {@link Operator}�ƁA�������\���g�[�N���̑Ή��B
	 */
	private static final Map<String, Operator> OPERATORS = new HashMap<String, Operator>(16, 1.0f);
	/**
	 * ������K�v�Ȃ��̂�<a href="http://en.wikipedia.org/wiki/Singleton_pattern">Singleton</a>�B
	 * 
	 */
	private static ResultantFactory mSingleton = new ResultantFactory();
	/**
	 * ���ɍs��{@link Operator}�B
	 */
	private Operator mOperator; 
	
	/**
	 * {@link Operator#ASSOC}��arity��3�B
	 */
	private static final int ASSOC_ARRITY = 3;
	
	static {
		OPERATORS.put("+", Operator.ADD);
		OPERATORS.put("-", Operator.SUB);
		OPERATORS.put("*", Operator.MUL);
		OPERATORS.put("/", Operator.DIV);
		OPERATORS.put("^", Operator.POW);
		OPERATORS.put("<=", Operator.SUBSTITUTION);
		OPERATORS.put("norm", Operator.NORM);
		OPERATORS.put("conj", Operator.CONJ);
		OPERATORS.put("negate", Operator.NEGATE);
		OPERATORS.put("inv", Operator.INV);
		OPERATORS.put("commu", Operator.COMMU);
		OPERATORS.put("assoc", Operator.ASSOC);
		OPERATORS.put("normed", Operator.NORMED);
	};
	/**
	 * �O������C���X�^���X���ł��Ȃ��悤�ɂ��Ă����B
	 */
	private ResultantFactory() {
	}
	/**
	 * 
	 * @return {@link VariableFactory}��<a href="http://en.wikipedia.org/wiki/Singleton_pattern">Singleton</a>�B
	 * 
	 */
	public static ResultantFactory getInstance() {
		return mSingleton;
	}
	@Override
	public boolean getReady(final String aToken) throws BackgroundProcessCancelledException {
		if (super.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		mOperator = OPERATORS.get(aToken);
		if (mOperator == null) {
			return false;
		}
		return true;
	}
	@Override
	public void calc() throws CalculatingException, BackgroundProcessCancelledException {
		Deque<Operand> tStack = getStack();
		switch (mOperator) {
		case ADD:
			tStack.push(add());
			return;
		case SUB:
			tStack.push(sub());
			return;
		case MUL:
			tStack.push(mul());
			return;
		case DIV:
			tStack.push(div());
			return;
		case POW:
			tStack.push(pow());
			return;
		case SUBSTITUTION:
			tStack.push(substitution());
			return;
		case NORM:
			tStack.push(norm());
			return;
		case CONJ:
			tStack.push(conj());
			return;
		case NEGATE:
			tStack.push(negate());
			return;
		case INV:
			tStack.push(inv());
			return;
		case COMMU:
			tStack.push(commu());
			return;
		case ASSOC:
			tStack.push(assoc());
			return;
		case NORMED:
			tStack.push(normed());
			return;
		default:
		}
	}
	
	/**
	 * Java�̈����̕]�������͍����珇�ɂł���B
	 * @return {@link Factory#getStack()}������o���ꂽ��̒l�̘a
	 * @throws CalculatingException {@link Factory#getStack()}�ɐς܂ꂽ+�̂��߂̈��������Ȃ�����B
	 * @throws BackgroundProcessCancelledException �o�b�N�O���E���h�������L�����Z�����ꂽ�Ƃ��̗�O�B
	 */
	private Constant add() throws CalculatingException, BackgroundProcessCancelledException {
		if (super.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 2) {
			throw new CalculatingException(R.string.add_arity_error);
		}
		Operand tOperand2 = tStack.pop();
		Operand tOperand1 = tStack.pop();
		if (tOperand1.isZero()) {
			return tOperand2.getInterior();
		}
		if (tOperand2.isZero()) {
			return tOperand1.getInterior();
		}
		int tHeight1 = tOperand1.getHeight();
		int tHeight2 = tOperand2.getHeight();
		if (tHeight1 > tHeight2) {
			// �Ō�ɏo�������́B
			tStack.push(tOperand1.getImag());
			// ���Z��������
			tStack.push(tOperand1.getReal());
			tStack.push(tOperand2);
			tOperand1 = null;
			tOperand2 = null;
			return mImaginaryFactory.mixRealAndImaginary(add(), tStack.pop().getInterior(), tHeight1);
		} else if (tHeight2 > tHeight1) {
			// �Ō�ɏo�������́B
			tStack.push(tOperand2.getImag());
			// ���Z�������́B
			tStack.push(tOperand1);
			tStack.push(tOperand2.getReal());
			tOperand1 = null;
			tOperand2 = null;
			return mImaginaryFactory.mixRealAndImaginary(add(), tStack.pop().getInterior(), tHeight2);
		} else if (tHeight1 == 0) {
			return tOperand1.add(tOperand2);
		} else {
			// �Ō�ɉ��Z��������
			tStack.push(tOperand1.getImag());
			tStack.push(tOperand2.getImag());
			// �ŏ��ɉ��Z��������
			tStack.push(tOperand1.getReal());
			tStack.push(tOperand2.getReal());
			tOperand1 = null;
			tOperand2 = null;
			return mImaginaryFactory.mixRealAndImaginary(add() , add(), tHeight1);
		}
	}
	/**
	 * Java�̈����̕]�������͍����珇�ɂł���B
	 * @return {@link Factory#getStack()}������o���ꂽ��̒l�̐�
	 * @throws CalculatingException {@link Factory#getStack()}�ɐς܂ꂽ*�̂��߂̈��������Ȃ�����B
	 * @throws BackgroundProcessCancelledException �o�b�N�O���E���h�������L�����Z�����ꂽ�Ƃ��̗�O�B
	 */
	private Constant mul() throws CalculatingException, BackgroundProcessCancelledException {
		if (super.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 2) {
			throw new CalculatingException(R.string.multiply_arity_error);
		}
		Operand tOperand2 = tStack.pop();
		Operand tOperand1 = tStack.pop();
		if (tOperand1.isZero() || tOperand2.isZero()) {
			return Zero.ZERO;
		}
		int tHeight1 = tOperand1.getHeight();
		int tHeight2 = tOperand2.getHeight();
		// �������ׂāA0�ɂ��|���Z���ȗ�����B
		// (p,q)(r,s)=(p r - s^* q, s p + q r^*)�ł���B
		// �P���ɕ��z����K�p���Ȃ��悤�ɒ��ӁB
		if (tHeight1 > tHeight2) { // ����̂ق����������Ⴂ�Ƃ��B
			// �Ō�ɉ��Z��������
			tStack.push(tOperand1.getImag());
			tStack.push(tOperand2);
			tStack.push(conj());
			// �ŏ��ɉ��Z��������
			tStack.push(tOperand1.getReal());
			tStack.push(tOperand2);
			tOperand1 = null;
			tOperand2 = null;
			return mImaginaryFactory.mixRealAndImaginary(mul(), mul(), tHeight1);
		} else if (tHeight2 > tHeight1) { // ����̂ق��������������Ƃ��B
			// �Ō�ɉ��Z��������
			tStack.push(tOperand2.getImag());
			tStack.push(tOperand1);
			// �ŏ��ɉ��Z��������
			tStack.push(tOperand1);
			tStack.push(tOperand2.getReal());
			tOperand1 = null;
			tOperand2 = null;
			return mImaginaryFactory.mixRealAndImaginary(mul(), mul(), tHeight2);
		} else if (tHeight1 == 0) {
			return tOperand1.mul(tOperand2);
		} else {
			// �Ō�ɉ��Z��������
			tStack.push(tOperand2.getImag());
			tStack.push(tOperand1.getReal());
			tStack.push(mul()); // s p���i�[
			tStack.push(tOperand1.getImag());
			tStack.push(tOperand2.getReal());
			tStack.push(conj());
			tStack.push(mul()); // q r^*���i�[
			// �ŏ��ɉ��Z�������́B
			tStack.push(tOperand1.getReal());
			tStack.push(tOperand2.getReal());
			tStack.push(mul()); // p r���i�[
			tStack.push(tOperand2.getImag());
			tStack.push(conj());
			tStack.push(tOperand1.getImag());
			tOperand1 = null;
			tOperand2 = null;
			tStack.push(mul());
			tStack.push(negate()); // s^* q���i�[
			return mImaginaryFactory.mixRealAndImaginary(add(), add(), tHeight1);
		}
	}
	
	/**
	 * Java�̈����̕]�������͍����珇�ɂł���B
	 * @return {@link Factory#getStack()}������o���ꂽ��̒l�̗ݏ�
	 * @throws CalculatingException {@link Factory#getStack()}�ɐς܂ꂽ^�̂��߂̈��������Ȃ�����B<br>
	 * �܂��́A��ڂ̈����������łȂ��B
	 * @throws BackgroundProcessCancelledException �o�b�N�O���E���h�������L�����Z�����ꂽ�Ƃ��̗�O�B
	 */
	private Constant pow() throws CalculatingException, BackgroundProcessCancelledException {
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 2) {
			throw new CalculatingException(R.string.power_arity_error);
		}
		Operand tOperand2 = tStack.pop();
		if (!tOperand2.isInteger()) {
			throw new CalculatingException(R.string.power_not_integer_error);
		}
		BigInteger tPow = tOperand2.getInteger();
		tOperand2 = null;
		Operand tBase = tStack.pop();
		Constant tResult = BaseFieldFactory.getInstance().getOne();
		if (tPow.compareTo(BigInteger.ZERO) < 0) {
			tPow = tPow.negate();
			tBase = tBase.inv();
		}
		while (tPow.compareTo(BigInteger.ZERO) > 0) {
			if (super.isCanceled()) {
				throw new BackgroundProcessCancelledException();
			}
			if (tPow.getLowestSetBit() == 0) { // �Q�i���\�L�ň�ԉE�����܂��Ă���A�Ƃ������Ƃ͂��ꂪ��ł��邱�Ƃ��Ӗ�����B
				tStack.push(tBase);
				tStack.push(tResult);
				tResult = mul();
				tPow = tPow.subtract(BigInteger.ONE);
			} else {
				tStack.push(tBase);
				tStack.push(tBase);
				tBase = mul();
				tPow = tPow.shiftRight(1);
			}
		}
		return tResult;
	}
	/**
	 * 
	 * @return {@link Factory#getStack()}������o���ꂽ�l�̕����𔽓]�������́B
	 * @throws CalculatingException {@link Factory#getStack()}�ɐς܂ꂽminus�̂��߂̈��������Ȃ�����B
	 * @throws BackgroundProcessCancelledException �o�b�N�O���E���h�������L�����Z�����ꂽ�Ƃ��̗�O�B
	 */
	private Constant negate() throws CalculatingException, BackgroundProcessCancelledException {
		if (super.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 1) {
			throw new CalculatingException(R.string.negate_arity_error);
		}
		Operand tOperand = tStack.pop();
		int tHeight = tOperand.getHeight();
		if (tHeight == 0) {
			return tOperand.negate();
		} else {
			tStack.push(tOperand.getImag());
			tStack.push(tOperand.getReal());
			tOperand = null;
			return mImaginaryFactory.mixRealAndImaginary(negate(), negate(), tHeight);
		}
	}
	/**
	 * Java�̈����̕]�������͍����珇�ɂł���B
	 * @return {@link Factory#getStack()}������o���ꂽ��̒l�̍�
	 * @throws CalculatingException {@link Factory#getStack()}�ɐς܂ꂽ-�̂��߂̈��������Ȃ�����B
	 * @throws BackgroundProcessCancelledException �o�b�N�O���E���h�������L�����Z�����ꂽ�Ƃ��̗�O�B
	 */
	private Constant sub() throws CalculatingException, BackgroundProcessCancelledException {
		if (super.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 2) {
			throw new CalculatingException(R.string.substruct_arity_error);
		}
		tStack.push(negate());
		return add();
	}
	/**
	 * 
	 * @return {@link Factory#getStack()}������o���ꂽ�l�̋����B
	 * @throws CalculatingException {@link Factory#getStack()}�ɐς܂ꂽconj�̂��߂̈��������Ȃ�����B
	 * @throws BackgroundProcessCancelledException �o�b�N�O���E���h�������L�����Z�����ꂽ�Ƃ��̗�O�B
	 */
	private Constant conj() throws CalculatingException, BackgroundProcessCancelledException {
		if (super.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 1) {
			throw new CalculatingException(R.string.conj_arity_error);
		}
		Operand tOperand = tStack.pop();
		int tHeight = tOperand.getHeight();
		if (tHeight == 0) {
			return tOperand.getInterior();
		} else {
			tStack.push(tOperand.getImag());
			tStack.push(tOperand.getReal());
			tOperand = null;
			return mImaginaryFactory.mixRealAndImaginary(conj(), negate(), tHeight);
		}
	}
	/**
	 * 
	 * @return {@link Factory#getStack()}������o���ꂽ�l�̃m����
	 * @throws CalculatingException {@link Factory#getStack()}�ɐς܂ꂽnorm�̂��߂̈��������Ȃ�����B
	 * @throws BackgroundProcessCancelledException �o�b�N�O���E���h�������L�����Z�����ꂽ�Ƃ��̗�O�B
	 */
	private Constant norm() throws CalculatingException, BackgroundProcessCancelledException {
		if (super.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 1) {
			throw new CalculatingException(R.string.norm_arity_error);
		}
		Operand tOperand = tStack.pop();
		if (tOperand.getHeight() == 0) {
			return tOperand.mul(tOperand);
		} else {
			tStack.push(tOperand.getImag());
			tStack.push(norm());
			tStack.push(tOperand.getReal());
			tOperand = null;
			tStack.push(norm());
			return tStack.pop().add(tStack.pop());
		}
	}
	/**
	 * 
	 * @return {@link Factory#getStack()}������o���ꂽ�l�̋t���B
	 * @throws CalculatingException {@link Factory#getStack()}�ɐς܂ꂽinv�̂��߂̈��������Ȃ�����B
	 * @throws BackgroundProcessCancelledException �o�b�N�O���E���h�������L�����Z�����ꂽ�Ƃ��̗�O�B
	 */
	private Constant inv() throws CalculatingException, BackgroundProcessCancelledException {
		if (super.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 1) {
			throw new CalculatingException(R.string.inv_arity_error);
		}
		Operand tOperand = tStack.pop();
		if (tOperand.getHeight() == 0) {
			return tOperand.inv();
		}
		tStack.push(tOperand);
		tStack.push(conj());
		tStack.push(tOperand);
		tStack.push(norm());
		tOperand = null;
		return div();
	}
	/**
	 * Java�̈����̕]�������͍����珇�ɂł���B
	 * @return {@link Factory#getStack()}������o���ꂽ��̒l�̏�
	 * @throws CalculatingException {@link Factory#getStack()}�ɐς܂ꂽ/�̂��߂̈��������Ȃ�����B
	 * @throws BackgroundProcessCancelledException �o�b�N�O���E���h�������L�����Z�����ꂽ�Ƃ��̗�O�B
	 */
	private Constant div() throws CalculatingException, BackgroundProcessCancelledException {
		if (super.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 2) {
			throw new CalculatingException(R.string.divide_arity_error);
		}
		Operand tOperand2 = tStack.pop();
		if (tOperand2.getHeight() == 0) {
			if (tOperand2.isZero()) {
				throw new CalculatingException(R.string.divide_by_zero);
			}
			Operand tOperand1 = tStack.pop();
			int tHeight = tOperand1.getHeight();
			if (tHeight == 0) {
				return tOperand1.div(tOperand2);
			}
			// �Ō�ɉ��Z��������
			tStack.push(tOperand1.getImag());
			tStack.push(tOperand2);
			// �ŏ��ɉ��Z�������́B
			tStack.push(tOperand1.getReal());
			tStack.push(tOperand2);
			tOperand1 = null;
			return mImaginaryFactory.mixRealAndImaginary(div(), div(), tHeight);
		}
		tStack.push(inv());
		return mul();
	}
	/**
	 * Java�̈����̕]�������͍����珇�ɂł���B
	 * @return {@link Factory#getStack()}������o���ꂽ��̒l��a,b��ab-ba
	 * @throws CalculatingException {@link Factory#getStack()}�ɐς܂ꂽcommu�̂��߂̈��������Ȃ�����B
	 * @throws BackgroundProcessCancelledException �o�b�N�O���E���h�������L�����Z�����ꂽ�Ƃ��̗�O�B
	 */
	private Constant commu() throws CalculatingException, BackgroundProcessCancelledException {
		if (super.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 2) {
			throw new CalculatingException(R.string.commu_arity_error);
		}
		Operand tOperand2 = tStack.pop();
		Operand tOperand1 = tStack.peek();
		tStack.push(tOperand2);
		tStack.push(mul());
		tStack.push(tOperand2);
		tStack.push(tOperand1);
		tOperand1 = null;
		tOperand2 = null;
		tStack.push(mul());
		return sub();
	}
	/**
	 * Java�̈����̕]�������͍����珇�ɂł���B
	 * @return {@link Factory#getStack()}������o���ꂽ�O�̒l��(ab)c-a(bc)
	 * @throws CalculatingException {@link Factory#getStack()}�ɐς܂ꂽassoc�̂��߂̈��������Ȃ�����B
	 * @throws BackgroundProcessCancelledException �o�b�N�O���E���h�������L�����Z�����ꂽ�Ƃ��̗�O�B
	 */
	private Constant assoc() throws CalculatingException, BackgroundProcessCancelledException {
		if (super.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < ASSOC_ARRITY) {
			throw new CalculatingException(R.string.assoc_arity_error);
		}
		Operand tOperand3 = tStack.pop();
		Operand tOperand2 = tStack.pop();
		Operand tOperand1 = tStack.peek();
		tStack.push(tOperand2);
		tStack.push(mul());
		tStack.push(tOperand3);
		tStack.push(mul());
		tStack.push(tOperand1);
		tStack.push(tOperand2);
		tStack.push(tOperand3);
		tOperand1 = null;
		tOperand2 = null;
		tOperand3 = null;
		tStack.push(mul());
		tStack.push(mul());
		return sub();
	}
	/**
	 * Java�̈����̕]�������͍����珇�ɂł���B
	 * @return {@link Factory#getStack()}������o���ꂽ��̒la,b��|ab|-|a||b|
	 * @throws CalculatingException {@link Factory#getStack()}�ɐς܂ꂽnormed�̂��߂̈��������Ȃ�����B
	 * @throws BackgroundProcessCancelledException �o�b�N�O���E���h�������L�����Z�����ꂽ�Ƃ��̗�O�B
	 */
	private Constant normed() throws CalculatingException, BackgroundProcessCancelledException {
		if (super.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 2) {
			throw new CalculatingException(R.string.normed_arity_error);
		}
		Operand tOperand2 = tStack.pop();
		Operand tOperand1 = tStack.peek();
		tStack.push(tOperand2);
		tStack.push(mul());
		tStack.push(norm());
		tStack.push(tOperand1);
		tStack.push(norm());
		tStack.push(tOperand2);
		tOperand1 = null;
		tOperand2 = null;
		tStack.push(norm());
		tStack.push(mul());
		return sub();
	}
	/**
	 * Java�̈����̕]�������͍����珇�ɂł���B
	 * @return {@link Factory#getStack()}������o���ꂽ��̒l�̓�ڂɁA��ڂ�������B
	 * @throws CalculatingException {@link Factory#getStack()}�ɐς܂ꂽ<=�̂��߂̈��������Ȃ�����B
	 * @throws BackgroundProcessCancelledException �o�b�N�O���E���h�������L�����Z�����ꂽ�Ƃ��̗�O�B
	 */
	private Operand substitution() throws CalculatingException, BackgroundProcessCancelledException {
		if (super.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 2) {
			throw new CalculatingException(R.string.substitution_arity_error);
		}
		Operand tOperand2 = tStack.pop();
		Operand tOperand1 = tStack.pop();
		tOperand1.setInterior(tOperand2.getInterior());
		return tOperand1;
	}
}
