module.exports = {
	median: function (positions, n){
		var ret = [];
		n = Math.floor(n/2);
		for (var i=0; i < positions.length; i++) {
			arr = positions.slice(i < n ? 0:i-n,i+n).filter(e => e != null);
			
			var lats = arr.map(e => e[0]).sort();
			var longs = arr.map(e => e[1]).sort();
			
			var ll = Math.floor(lats.length/2)
			ret.push([lats[ll], longs[ll]]);
		}
		
		return ret;
	},
	mean: function (positions, n){
		var ret = [];
		n = Math.floor(n/2);
		for (var i=0; i < positions.length; i+= n) {
			arr = positions.slice(i < n ? 0:i-n,i+n).filter(e => e != null);
			ret.push(arr.reduce((l1,l2) => [l1[0] + l2[0],l1[1] + l2[1]]).map(e => e/arr.length));
		}
		return ret;
	}
}