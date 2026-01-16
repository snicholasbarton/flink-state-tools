import json
from py4j.java_gateway import JavaGateway

class Savepoint:
    def __init__(self, analyzer):
        self._analyzer = analyzer

    def get_topology(self):
        """Returns the topology as a Python dictionary."""
        json_str = self._analyzer.getTopologyJson()
        return json.loads(json_str)

    def get_skew_metrics(self, uid):
        json_str = self._analyzer.getSkewMetrics(uid)
        return json.loads(json_str)

class FlinkStateClient:
    def __init__(self):
        self.gateway = JavaGateway()
        self.entry_point = self.gateway.entry_point

    def create_analyzer(self, path):
        java_analyzer = self.entry_point.createStateAnalyzer()
        java_analyzer.loadSavepoint(path)
        return Savepoint(java_analyzer)
