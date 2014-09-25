package jp.tannakaken.infinitenion.operand;
/**
 * オペレータを表す列挙体。
 * @author tannakaken
 *
 */
enum Operator {
	/**
	 * 和。
	 */
	ADD,
	/**
	 * 差。
	 */
	SUB,
	/**
	 * 積。
	 */
	MUL,
	/**
	 * 商。
	 */
	DIV,
	/**
	 * 累乗。
	 */
	POW,
	/**
	 * 代入。
	 */
	SUBSTITUTION,
	/**
	 * ノルム。
	 */
	NORM,
	/**
	 * 共役。
	 */
	CONJ,
	/**
	 * 符号反転。
	 */
	NEGATE,
	/**
	 * 逆数。
	 */
	INV,
	/**
	 * 可換。
	 */
	COMMU,
	/**
	 * 結合。
	 */
	ASSOC,
	/**
	 * ノルム付け。
	 */
	NORMED,
}
