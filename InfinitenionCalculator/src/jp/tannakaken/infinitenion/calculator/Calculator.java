package jp.tannakaken.infinitenion.calculator;

import java.util.Random;

import android.os.AsyncTask;
import jp.tannakaken.infinitenion.R;
import jp.tannakaken.infinitenion.operand.Factory;
import jp.tannakaken.infinitenion.operand.ImaginaryFactory;
import jp.tannakaken.infinitenion.operand.BaseFieldFactory;
import jp.tannakaken.infinitenion.operand.VariableFactory;
import jp.tannakaken.infinitenion.operand.ResultantFactory;

/**
 * ���͂��ꂽ����������߂��A���s���A�o�͂���镶�����Ԃ��N���X�B<br>
 * ���ۂ̌v�Z�́A���܂��܂ȃN���X�ɈϏ������B<br>
 * ��̓I�ɂ́A�e��{@link Operand}�̃C���X�^���X�����́A����ɑΉ�����{@link Factory}�̃T�u�N���X�ɁA�Ϗ�����A<br>
 * ���Z�̌^�`�F�b�N��{@link Operator}�̃C���X�^���X���s���A�v�Z��{@link Constant}�̃C���X�^���X�������ōs���B<br>
 * ����ɂ���āA���̃N���X���g�́A���ɒP���ɂȂ��Ă���B<br>
 * ����́A{@link MainActivity}���N���C�A���g�Ƃ����AFacade�p�^�[���̗��p�ɂ����̂ł���B
 * 
 * @author tannakaken
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Facade_pattern">http://en.wikipedia.org/wiki/Facade_pattern</a>
 */
public class Calculator {
	
	/**
	 * �ϐ������Factory�B
	 */
	private VariableFactory mVariableFactory = VariableFactory.getInstance();
	/**
	 * �萔�����Factory�B
	 */
	private ImaginaryFactory mConstantFactory = ImaginaryFactory.getInstance();
	/**
	 * �������Factory�B
	 */
	private BaseFieldFactory mNumberFactory = BaseFieldFactory.getInstance();
	/**
	 * �X�^�b�N�ɗ��܂����I�y�����h����A�I�y���[�^�ɑΉ���������ŁA�V�����I�y�����h�����Factory�B
	 */
	private ResultantFactory mResultantFactory = ResultantFactory.getInstance();
	
	/**
	 * 
	 * @param aFormula �v�Z����鎮�B
	 * @param aTask ���̌v�Z�����s���Ă���񓯊��^�X�N�B
	 * @return �����v�Z�����l�̕�����B
	 * @throws CalculatorParseException �p�[�X�̎��s
	 * @throws CalculatingException �v�Z�̎��s
	 * @throws BackgroundProcessCancelledException �o�b�N�O���E���h�������L�����Z�����ꂽ�Ƃ��̗�O�B
	 */
	public final String calc(final String aFormula, final AsyncTask<String, Void, String> aTask)
			throws CalculatorParseException, CalculatingException, BackgroundProcessCancelledException {
		try {
			String tTrimed =  aFormula.replaceFirst("^[��]*", "");
			if (tTrimed == "") { // �C�[�X�^�[�G�b�O
				return randomAphorism();
			}
			String[] tTokens = tTrimed.split("[��]+");
			Factory.setTask(aTask);
			for (String tToken: tTokens) {
				if (aTask.isCancelled()) {
					throw new BackgroundProcessCancelledException();
				}
				if (mResultantFactory.getReady(tToken)) {
					mResultantFactory.calc();
				} else if (mVariableFactory.getReady(tToken)) {
					mVariableFactory.calc();
				} else if (mConstantFactory.getReady(tToken)) {
					mConstantFactory.calc();
				} else if (mNumberFactory.getReady(tToken)) {
					mNumberFactory.calc();
				} else {
					throw new CalculatorParseException(R.string.illegal_token, tToken);
				}
			}
			return Factory.getResult();
		} catch (CalculatorParseException | CalculatingException | BackgroundProcessCancelledException e) {
			throw e;
		} finally {
			Factory.clearStack();
		}
	}
	
	
	/**
	 * �C�[�X�^�[�G�b�O�p�̊i���W�B
	 */
	private String[] mAphorisms = {"There's more than one way to do it.",
								   "The world is full of fascinating problems waiting to be solved.",
								   "If you have the right attitude, interesting problems will find you.",
								   "And now for something completely different ...",
								   "... and yes I said yes I will Yes."};
	/**
	 * �C�[�X�^�[�G�b�O�p�̗����B
	 */
	private Random mRandom = new Random();
	/**
	 * �C�[�X�^�[�G�b�O�p�̃��\�b�h�B<br>
	 * ���͂���̂Ƃ��A�i���������_���ɕԂ��B
	 * @return �����_���Ȋi��
	 */
	private String randomAphorism() {
		return mAphorisms[mRandom.nextInt(mAphorisms.length)];
	}
	
}
