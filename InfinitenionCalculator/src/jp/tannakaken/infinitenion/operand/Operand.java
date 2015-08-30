package jp.tannakaken.infinitenion.operand;

import java.math.BigInteger;

import jp.tannakaken.infinitenion.calculator.CalculatingException;

/**
 * 被演算子を表すクラス。
 * @author tannakaken
 *
 */
interface Operand {

	/**
	 * {@link Operand#getHeight()}の値が1以上のインスタンスにのみ定義される。
	 * @return この元の実部
	 */
	Constant getReal();
	/**
	 * {@link Operand#getHeight()}の値が1以上のインスタンスにのみ定義される。
	 * @return この元の虚部
	 */
	Constant getImag();
	/**
	 * {@link Constant#drop}で木の高さが変わっても、数の解釈が変わらないように、木の高さを保持するメソッド。<br>
	 * この値を比較することにより、0による足し算や0による掛け算を出来るだけ省略するべきである。
	 * 
	 * @return この代数の階層。すなわち、有理数上2^n次元ならば、nを返す。
	 */
	int getHeight();
	/**
	 * もしこの元が変数{@link Variable}なら、 定数 aInterior を代入する。<br>
	 * 定数{@link Constant}なら、例外を投げる。
	 * 
	 * @param aInterior 代入される定数
	 * @throws CalculatingException 変数でない元に代入を行おうとしたときに発生する例外。
	 */
	void setInterior(Constant aInterior) throws CalculatingException;
	/**
	 * このインターフェースを実装したインスタントが変数{@link Variable}なら、その中身を、
	 * 定数{@link Constant}なら、自分自身を返す。
	 * @return 変数{@link Variable}の中身。もしくは定数{@link Constant}の自分自身。
	 */
	Constant getInterior();
	/**
	 * 整数が必要なときに、整数かどうかを判定するメソッド。<br>
	 * 整数が必要な場面で、整数でない元があった場合に適切な例外を投げるために必要。<br>、
	 * {@link Operand#getInteger()}をする直前に、必ずこのメソッドで検査すること。<br>
	 * それをしなかった場合の挙動は一切保証しない。<br>
	 * 具体的には、{@link UnsupportedOperationException}等の非チェック例外が出るなどして、アプリが異常停止する。
	 * @return 整数かどうか。
	 */
	boolean isInteger();
	/**
	 * このメソッドを使用する直前に必ず、{@link Operand#isInteger()}で整数かどうか検査すること。<br>
	 * それをしなかった場合の挙動は一切保証しない。<br>
	 * 具体的には、意味のない値を返したり、{@link UnsupportedOperationException}等の非チェック例外が出るなどして、アプリが異常停止する。
	 * @return もしこれが整数なら、整数を返す。
	 * 
	 */
	BigInteger getInteger();
	/**
	 * 
	 * @return インスタンスが0を表しているかどうか。
	 */
	boolean isZero();
	/**
	 * {@link Operand#getHeight()}の値が0のインスタンス同士にのみ定義される。
	 * それ以外は、staticメソッド{@link ResultantFactory#add()}によって計算される。
	 * @param aOperand このインスタンスに足されるインスタンス
	 * @return 和
	 */
	Constant add(Operand aOperand);
	/**
	 * {@link Operand#getHeight()}の値が0のインスタンス同士にのみ定義される。
	 * それ以外は、staticメソッド{@link ResultantFactory#mul()}によって計算される。
	 * @param aOperand このインスタンスに掛けられるインスタンス
	 * @return 積
	 */
	Constant mul(Operand aOperand);
	/**
	 * {@link Operand#getHeight()}の値が0のインスタンスにのみ定義される。
	 * それ以外は、staticメソッド{@link ResultantFactory#negate()}によって計算される。
	 * @return 符号反転
	 */
	Constant negate();
	/**
	 * {@link Operand#getHeight()}の値が0のインスタンスにのみ定義される。
	 * それ以外は、staticメソッド{@link ResultantFactory#div()}によって計算される。
	 * * @param aOperand このインスタンスを割るインスタンス
	 * @return 商
	 */
	Constant div(Operand aOperand);
	/**
	 * {@link Operand#getHeight()}の値が0のインスタンスにのみ定義される。
	 * それ以外は、staticメソッド{@link ResultantFactory#inv()}によって計算される。
	 * @return 逆数
	 * @throws CalculatingException 0の逆数を求めようとしたときに発生する例外
	 */
	Constant inv() throws CalculatingException;
}
