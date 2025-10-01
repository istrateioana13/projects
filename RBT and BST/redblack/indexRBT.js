const tree = new Tree.RedBlack();

function setup() {
	const container = document.getElementById("container");
	const canvas = createCanvas(0, 0);
	canvas.parent(container);
	canvas.resize(container.clientWidth, container.clientHeight);

	const controls = document.getElementById("controls");
	const add = createInputWithButton("ADD", (input) => {
		const num = parseInt(input.value());
		if (isNaN(num)) return;
		tree.add(num);
		redraw();
	});
	const remove = createInputWithButton("REMOVE", (input) => {
		const num = parseInt(input.value());
		if (isNaN(num)) return;
		tree.remove(num);
		redraw();
	});
	add.parent(controls);
	remove.parent(controls);
	noLoop();
}

function draw() {
	background(239, 234, 234);
	tree.draw(width / 2, Tree._size);
}

function createInputWithButton(str, onPress) {
	const cont = createDiv();

	const input = createInput();
	input.parent(cont);

	const button = createButton(str);
	button.elt.onmousedown = () => {
		onPress(input);
		input.value("");
	};
	button.parent(cont);

	input.elt.onkeydown = (e) => {
		if (e.key === "Enter") {
			button.elt.onmousedown();
		}
	};

	return cont;
}
