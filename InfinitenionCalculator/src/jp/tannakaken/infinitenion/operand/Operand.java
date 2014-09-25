package jp.tannakaken.infinitenion.operand;

import java.math.BigInteger;

import jp.tannakaken.infinitenion.calculator.CalculatingException;

/**
 * �퉉�Z�q��\���N���X�B
 * @author tannakaken
 *
 */
interface Operand {

	/**
	 * {@link Operand#getHeight()}�̒l��1�ȏ�̃C���X�^���X�ɂ̂ݒ�`�����B
	 * @return ���̌��̎���
	 */
	Constant getReal();
	/**
	 * {@link Operand#getHeight()}�̒l��1�ȏ�̃C���X�^���X�ɂ̂ݒ�`�����B
	 * @return ���̌��̋���
	 */
	Constant getImag();
	/**
	 * {@link Constant#drop}�Ŗ؂̍������ς���Ă��A���̉��߂��ς��Ȃ��悤�ɁA�؂̍�����ێ����郁�\�b�h�B<br>
	 * ���̒l���r���邱�Ƃɂ��A0�ɂ�鑫���Z��0�ɂ��|���Z���o���邾���ȗ�����ׂ��ł���B
	 * 
	 * @return ���̑㐔�̊K�w�B���Ȃ킿�A�L������2^n�����Ȃ�΁An��Ԃ��B
	 */
	int getHeight();
	/**
	 * �������̌����ϐ�{@link Variable}�Ȃ�A �萔 aInterior ��������B<br>
	 * �萔{@link Constant}�Ȃ�A��O�𓊂���B
	 * 
	 * @param aInterior ��������萔
	 * @throws CalculatingException �ϐ��łȂ����ɑ�����s�����Ƃ����Ƃ��ɔ��������O�B
	 */
	void setInterior(Constant aInterior) throws CalculatingException;
	/**
	 * ���̃C���^�[�t�F�[�X�����������C���X�^���g���ϐ�{@link Variable}�Ȃ�A���̒��g���A
	 * �萔{@link Constant}�Ȃ�A�������g��Ԃ��B
	 * @return �ϐ�{@link Variable}�̒��g�B�������͒萔{@link Constant}�̎������g�B
	 */
	Constant getInterior();
	/**
	 * �������K�v�ȂƂ��ɁA�������ǂ����𔻒肷�郁�\�b�h�B<br>
	 * �������K�v�ȏ�ʂŁA�����łȂ������������ꍇ�ɓK�؂ȗ�O�𓊂��邽�߂ɕK�v�B<br>�A
	 * {@link Operand#getInteger()}�����钼�O�ɁA�K�����̃��\�b�h�Ō������邱�ƁB<br>
	 * ��������Ȃ������ꍇ�̋����͈�ؕۏ؂��Ȃ��B<br>
	 * ��̓I�ɂ́A{@link UnsupportedOperationException}���̔�`�F�b�N��O���o��Ȃǂ��āA�A�v�����ُ��~����B
	 * @return �������ǂ����B
	 */
	boolean isInteger();
	/**
	 * ���̃��\�b�h���g�p���钼�O�ɕK���A{@link Operand#isInteger()}�Ő������ǂ����������邱�ƁB<br>
	 * ��������Ȃ������ꍇ�̋����͈�ؕۏ؂��Ȃ��B<br>
	 * ��̓I�ɂ́A�Ӗ��̂Ȃ��l��Ԃ�����A{@link UnsupportedOperationException}���̔�`�F�b�N��O���o��Ȃǂ��āA�A�v�����ُ��~����B
	 * @return �������ꂪ�����Ȃ�A������Ԃ��B
	 * 
	 */
	BigInteger getInteger();
	/**
	 * 
	 * @return �C���X�^���X��0��\���Ă��邩�ǂ����B
	 */
	boolean isZero();
	/**
	 * {@link Operand#getHeight()}�̒l��0�̃C���X�^���X���m�ɂ̂ݒ�`�����B
	 * ����ȊO�́Astatic���\�b�h{@link ResultantFactory#add()}�ɂ���Čv�Z�����B
	 * @param aOperand ���̃C���X�^���X�ɑ������C���X�^���X
	 * @return �a
	 */
	Constant add(Operand aOperand);
	/**
	 * {@link Operand#getHeight()}�̒l��0�̃C���X�^���X���m�ɂ̂ݒ�`�����B
	 * ����ȊO�́Astatic���\�b�h{@link ResultantFactory#mul()}�ɂ���Čv�Z�����B
	 * @param aOperand ���̃C���X�^���X�Ɋ|������C���X�^���X
	 * @return ��
	 */
	Constant mul(Operand aOperand);
	/**
	 * {@link Operand#getHeight()}�̒l��0�̃C���X�^���X�ɂ̂ݒ�`�����B
	 * ����ȊO�́Astatic���\�b�h{@link ResultantFactory#negate()}�ɂ���Čv�Z�����B
	 * @return �������]
	 */
	Constant negate();
	/**
	 * {@link Operand#getHeight()}�̒l��0�̃C���X�^���X�ɂ̂ݒ�`�����B
	 * ����ȊO�́Astatic���\�b�h{@link ResultantFactory#div()}�ɂ���Čv�Z�����B
	 * * @param aOperand ���̃C���X�^���X������C���X�^���X
	 * @return ��
	 */
	Constant div(Operand aOperand);
	/**
	 * {@link Operand#getHeight()}�̒l��0�̃C���X�^���X�ɂ̂ݒ�`�����B
	 * ����ȊO�́Astatic���\�b�h{@link ResultantFactory#inv()}�ɂ���Čv�Z�����B
	 * @return �t��
	 * @throws CalculatingException 0�̋t�������߂悤�Ƃ����Ƃ��ɔ��������O
	 */
	Constant inv() throws CalculatingException;
}
