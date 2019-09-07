package com.example.pc.dijkstraatkaist

class DijkstraUtil(private val graph: Graph) {
    private val adjacencyMatrix: Array<DoubleArray> by lazy {
        val matrix = Array(graph.nodes.size) { DoubleArray(graph.nodes.size) }
        graph.edges.forEach {
            matrix[it.first.idx][it.second.idx] = it.length
            matrix[it.second.idx][it.first.idx] = it.length
        }
        matrix
    }

    fun shortestPath(start: Node, end: Node): Pair<List<Edge>, Double> {
        val distances = DoubleArray(graph.nodes.size) { if (it == start.idx) 0.0 else Double.MAX_VALUE }
        val included = BooleanArray(graph.nodes.size)
        val parents = IntArray(graph.nodes.size) { if (it == start.idx) -1 else 0 }

        (0 until graph.nodes.size - 1).forEach { _ ->
            val filtered = distances.filterIndexed { index, _ -> !included[index] }
            val minIndex = distances.indexOf(filtered.min() as Double)
            included[minIndex] = true
            (0 until graph.nodes.size).forEach {
                if (!included[it]
                    && adjacencyMatrix[minIndex][it] != 0.0
                    && distances[minIndex] != Double.MAX_VALUE
                    && distances[minIndex] + adjacencyMatrix[minIndex][it] < distances[it]) {
                    distances[it] = distances[minIndex] + adjacencyMatrix[minIndex][it]
                    parents[it] = minIndex
                }
            }
        }

        val path = tracePath(end.idx, parents, mutableListOf())
        graph.edges.forEach { it.highlight = false }
        val edges = path.windowed(2, 1).map {
            val edge = graph.edges.find { edge ->
                edge == Edge(0, Node(it[0]), Node(it[1]))
            } as Edge
            edge.highlight = true
            edge
        }
        return Pair(edges, distances[end.idx])
    }

    private fun tracePath(from: Int, parents: IntArray, path: MutableList<Int>): MutableList<Int> {
        if (parents[from] == -1) {
            path.add(from)
            return path
        }
        path.add(from)
        return tracePath(parents[from], parents, path)
    }
}