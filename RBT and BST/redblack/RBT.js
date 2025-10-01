const Tree = {
	Node: class Node {
		constructor(int, left, right, parent) {
			this.value = int;
			this.left = left;
			this.right = right;
			this.parent = parent;
		}

		add(int) {
			if (this.value === int) {
				return null;
			}

			if (this.value < int) {
				if (this.right) {
					return this.right.add(int);
				} else {
					this.setRight(new Tree.Node(int));
					return this.right;
				}
			} else {
				if (this.left) {
					return this.left.add(int);
				} else {
					this.setLeft(new Tree.Node(int));
					return this.left;
				}
			}
		}

		remove(int) {
			if (int < this.value && this.left) {
				this.setLeft(this.left.remove(int));
			} else if (int > this.value && this.right) {
				this.setRight(this.right.remove(int));
			} else {
				if (!this.left) {
					return this.right;
				}
				if (!this.right) {
					return this.left;
				}

				this.value = Tree.minValue(this.right).value;

				this.setRight(this.right.remove(this.value));
			}
			return this;
		}

		
		equals(node) {
			this.value = node.value;
			this.left = node.left;
			this.right = node.right;
		}

		hasRed() {
			return (
				(this.left && this.left.isRed()) ||
				(this.right && this.right.isRed())
			);
		}

		is(node) {
			return node && node.value === this.value;
		}

		isLeft() {
			return this.is(this.parent.left);
		}

		isRight() {
			return this.is(this.parent.right);
		}

		isRoot() {
			return this.parent == null;
		}

		isRed() {
			return this.color === Tree.RED;
		}

		isBlack() {
			return this.color === Tree.BLACK;
		}

		sibling() {
			if (this.isLeft()) return this.parent.right;
			return this.parent.left;
		}

		uncle() {
			return this.parent.sibling();
		}

		grandparent() {
			return this.parent.parent;
		}

		setLeft(node) {
			this.left = node;
			if (node) node.parent = this;
		}

		setRight(node) {
			this.right = node;
			if (node) node.parent = this;
		}

		set(node) {
			const parent = node.parent;
			const value = node.value;
			const left = node.left;
			const right = node.right;

			this.parent = parent;
			this.value = value;
			this.setLeft(left);
			this.setRight(right);
		}

		toRed() {
			this.color = Tree.RED;
		}

		toBlack() {
			this.color = Tree.BLACK;
		}

		copy() {
			return new Tree.Node(
				this.value,
				this.left,
				this.right,
				this.parent
			);
		}

		find(int) {
			if (this.value === int) {
				return this;
			}
			if (int < this.value) {
				return this.left ? this.left.find(int) : null;
			}
			return this.right ? this.right.find(int) : null;
		}
	},

	BLACK: 0,
	RED: 1,
	_size: 50,

	width: (node) => {
		if (!node) return 0;
		let width = 1;
		if (node.left) width += Tree.width(node.left);
		if (node.right) width += Tree.width(node.right);
		return width;
	},

	leftWidth: (node) => {
		if (!node.left) return 0;
		if (!node.left.right) return 1;
		return Tree.width(node.left.right) + 1;
	},

	rightWidth: (node) => {
		if (!node.right) return 0;
		if (!node.right.left) return 1;
		return Tree.width(node.right.left) + 1;
	},
	
	minValue: (node) => {
		let cursor = node;
		while (cursor.left) {
			cursor = cursor.left;
		}
		return cursor;
	},
};

Tree.RedBlack = class {
	add(int) {
		if (!this.root) {
			this.root = new Tree.Node(int);
			this.root.toBlack();
			return true;
		}

		let inserted = this.root.add(int);
		if (!inserted) return false;
		inserted.toRed();

		this.repair(inserted);

		return true;
	}

	remove(int, node = this.root) {
		if (int < node.value && node.left) {
			node.setLeft(this.remove(int, node.left));
		} else if (int > node.value && node.right) {
			node.setRight(this.remove(int, node.right));
		} else {
			if (!node.right || !node.left) {
				return this._remove(node);
			}

			node.value = Tree.minValue(node.right).value;

			node.setRight(this.remove(node.value, node.right));
		}
		return node;
	}

	_remove(node) {
		const u = node.left ? node.left : node.right;
		const doubleBlack = (!u || u.isBlack()) && node.isBlack();

		if (node.isRoot()) {
			this.root = u;
			if (this.root) {
				this.root.parent = null;
				this.root.toBlack();
			}
			return null;
		}

		if (!u) {
			if (doubleBlack) {
				this.fixDoubleBlack(node);
			}
			return null;
		}

		if (node.isLeft()) {
			node.parent.setLeft(u);
		} else {
			node.parent.setRight(u);
		}

		if (doubleBlack) {
			this.fixDoubleBlack();
		} else {
			u.toBlack();
		}

		return u;
	}

	fixDoubleBlack(node) {
		if (node.isRoot()) return;
		const sib = node.sibling();
		const par = node.parent;

		if (!sib) {
			this.fixDoubleBlack(par);
			return;
		}

		if (sib.isRed()) {
			par.toRed();
			sib.toBlack();

			if (sib.isLeft()) {
				this.rightRotate(par);
			} else {
				this.leftRotate(par);
			}

			this.fixDoubleBlack(node);
		} else {
			if (sib.hasRed()) {
				if (sib.left && sib.left.isRed()) {
					if (sib.isLeft()) {
						sib.left.toRed();
						sib.color = par.color;
						this.rightRotate(par);
					} else {
						sib.left.color = par.color;
						this.rightRotate(sib);
						this.leftRotate(par);
					}
				} else {
					if (sib.isLeft()) {
						sib.right = par.color;
						this.leftRotate(sib);
						this.rightRotate(par);
					} else {
						sib.right.toRed();
						sib.color = par.color;
						this.leftRotate(par);
					}
				}
				par.toBlack();
			} else {
				sib.toRed();
				if (par.isBlack()) {
					this.fixDoubleBlack(par);
				} else {
					par.toBlack();
				}
			}
		}
	}

	repair(node) {
		if (!node.parent) {
			node.toBlack();
			this.root = node;
		} else if (node.parent.isRed()) {
			if (node.uncle() && node.uncle().isRed()) {
				node.parent.toBlack();
				node.uncle().toBlack();
				node.grandparent().toRed();
				this.repair(node.grandparent());
			} else {
				const p = node.parent;
				
				if (node.isLeft() && node.parent.isRight()) {
					this.rightRotate(node.parent);
					node.set(node.right);
					p.setRight(node);
				} else if (node.isRight() && node.parent.isLeft()) {
					this.leftRotate(node.parent);
					node.set(node.left);
					p.setLeft(node);
				}
				if (node.isLeft()) {
					this.rightRotate(node.grandparent());
				} else {
					this.leftRotate(node.grandparent());
				}

				node.parent.toBlack();
				node.sibling().toRed();
			}
		}
	}

	rightRotate(root) {
		const newRoot = root.left;
		const oldRoot = root.copy();

		newRoot.parent = oldRoot.parent;

		oldRoot.setLeft(newRoot.right);

		newRoot.setRight(oldRoot);

		root.set(newRoot);
	}

	leftRotate(root) {
		const newRoot = root.right;
		const oldRoot = root.copy();

		newRoot.parent = oldRoot.parent;

		oldRoot.setRight(newRoot.left);

		newRoot.setLeft(oldRoot);

		root.set(newRoot);
	}

	draw(x, y, node = this.root) {
		if (!node) {
			return;
		}

		if (node.left) {
			const xoff = Tree.leftWidth(node) * Tree._size;
			stroke(0);
			line(x, y, x - xoff, y + Tree._size);
			this.draw(x - xoff, y + Tree._size, node.left);
		}
		if (node.right) {
			const xoff = Tree.rightWidth(node) * Tree._size;
			stroke(0);
			line(x, y, x + xoff, y + Tree._size);
			this.draw(x + xoff, y + Tree._size, node.right);
		}

		strokeWeight(2);
		if (node.color === Tree.BLACK) {
			stroke(127);
			fill(0);
		} else {
			fill(255, 0, 0);
			stroke(100, 0, 0);
		}
		ellipse(x, y, Tree._size);

		fill(255);
		noStroke();
		textAlign(CENTER);
		textSize(Tree._size / 2);
		text(node.value, x, y + Tree._size / 8);
	}
};
