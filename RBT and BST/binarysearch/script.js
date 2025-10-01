
class Node {
    constructor(value) {
      this.value = value;
      this.left = null;
      this.right = null;
    }
  }
  
  class BinarySearchTree {
    constructor() {
      this.root = null;
    }

    add(value) {
      const newNode = new Node(value);
  
      if (this.root === null) {
        this.root = newNode;
      } else {
        this.insertNode(this.root, newNode);
      }
  
      this.drawTree();
    }
  
    insertNode(node, newNode) {
      if (newNode.value < node.value) {
        if (node.left === null) {
          node.left = newNode;
        } else {
          this.insertNode(node.left, newNode);
        }
      } else if (newNode.value > node.value) {
        if (node.right === null) {
          node.right = newNode;
        } else {
          this.insertNode(node.right, newNode);
        }
      }
    }
  
    remove(value) {
      this.root = this.removeNode(this.root, value);
      this.drawTree();
    }
  
    removeNode(node, key) {
      if (node === null) {
        return null;
      } else if (key < node.value) {
        node.left = this.removeNode(node.left, key);
        return node;
      } else if (key > node.value) {
        node.right = this.removeNode(node.right, key);
        return node;
      } else {
        if (node.left === null && node.right === null) {
          node = null;
          return node;
        }
  
        if (node.left === null) {
          node = node.right;
          return node;
        } else if (node.right === null) {
          node = node.left;
          return node;
        }
  
        const minNode = this.findMinNode(node.right);
        node.value = minNode.value;
        node.right = this.removeNode(node.right, minNode.value);
        return node;
      }
    }
  
    findMinNode(node) {
      if (node.left === null) {
        return node;
      } else {
        return this.findMinNode(node.left);
      }
    }
  
    drawTree() {
      const container = document.getElementById("container");
      container.innerHTML = "";
      this.drawNode(this.root, container, container.offsetWidth / 2, 170);
    }
  
    drawNode(node, container, x = 0, y = 0, level = 0) {
      if (node === null) {
        return;
      }
  
      const circle = document.createElement("div");
      circle.className = "node";
      circle.textContent = node.value;
      container.appendChild(circle);
  
      const circleWidth = circle.offsetWidth;
      const circleHeight = circle.offsetHeight;
      const levelHeight = 130; 
      const initialLevelWidth = 650; 
      const levelWidthFactor = 0.5; 
  
      const circleX = x - circleWidth / 2;
      const circleY = y - circleHeight / 2;
  
      circle.style.left = circleX + "px";
      circle.style.top = circleY + "px";
  
      const childY = y + levelHeight;
      const levelWidth = initialLevelWidth * Math.pow(levelWidthFactor, level);
  
      const leftChildX = x - levelWidth / 2;
      const rightChildX = x + levelWidth / 2;
  
      if (node.left !== null) {
        this.drawLine(circleX + circleWidth / 2, circleY + circleHeight, leftChildX, childY);
        this.drawNode(node.left, container, leftChildX, childY, level + 1);
      }
  
      if (node.right !== null) {
        this.drawLine(circleX + circleWidth / 2, circleY + circleHeight, rightChildX, childY);
        this.drawNode(node.right, container, rightChildX, childY, level + 1);
      }
    }
  
    drawLine(parentX, parentY, childX, childY) {
        const container = document.getElementById("container");
      
        const line = document.createElement("div");
        line.className = "line";
        container.appendChild(line);
      
        const deltaX = childX - parentX;
        const deltaY = childY - parentY;
        const distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
      
        const angle = Math.atan2(deltaY, deltaX) * 180 / Math.PI;
        line.style.width = distance + "px";
        line.style.transform = `rotate(${angle}deg)`;
        line.style.position = "absolute";
        line.style.top = parentY + "px";
        line.style.left = parentX + "px";
      }
  }
  
  const bst = new BinarySearchTree();

  const addButton = document.getElementById("btnAdd");
  addButton.addEventListener("click", () => {
    const value = parseInt(document.getElementById("numberInput").value);
    if (!isNaN(value)) {
      bst.add(value);
      numberInput.value = "";
    }
  });
  

  const removeButton = document.getElementById("btnRemove");
  removeButton.addEventListener("click", () => {
    const value = parseInt(document.getElementById("numberInput").value);
    if (!isNaN(value)) {
      bst.remove(value);
      numberInput.value = "";
    }
  });
