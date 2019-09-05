package com.example.pc.dijkstraatkaist

class Edge(f: Node, s: Node) {
    val first: Node = f
    val second: Node = s
    val length: Double
        get() {
            return 0.0
        }

    override fun toString(): String {
        return "Edge($first <=> $second)"
    }
}