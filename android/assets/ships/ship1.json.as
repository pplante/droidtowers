package
{
	import Box2D.Dynamics.*;
	import Box2D.Collision.*;
	import Box2D.Collision.Shapes.*;
	import Box2D.Common.Math.*;
    import flash.utils.Dictionary;

    public class PhysicsData extends Object
	{
		// ptm ratio
        public var ptm_ratio:Number = 2;
		
		// the physcis data 
		var dict:Dictionary;
		
        //
        // bodytype:
        //  b2_staticBody
        //  b2_kinematicBody
        //  b2_dynamicBody

        public function createBody(name:String, world:b2World, bodyType:uint, userData:*):b2Body
        {
            var fixtures:Array = dict[name];

            var body:b2Body;
            var f:Number;

            // prepare body def
            var bodyDef:b2BodyDef = new b2BodyDef();
            bodyDef.type = bodyType;
            bodyDef.userData = userData;

            // create the body
            body = world.CreateBody(bodyDef);

            // prepare fixtures
            for(f=0; f<fixtures.length; f++)
            {
                var fixture:Array = fixtures[f];

                var fixtureDef:b2FixtureDef = new b2FixtureDef();


                fixtureDef.density=fixture[0];
                fixtureDef.friction=fixture[1];
                fixtureDef.restitution=fixture[2];

                fixtureDef.filter.categoryBits = fixture[3];
                fixtureDef.filter.maskBits = fixture[4];
                fixtureDef.filter.groupIndex = fixture[5];
                fixtureDef.isSensor = fixture[6];

                var p:Number;
                var polygons:Array = fixture[8];
                for(p=0; p<polygons.length; p++)
                {
                    var polygonShape:b2PolygonShape = new b2PolygonShape();
                    polygonShape.SetAsArray(polygons[p], polygons[p].length);
                    fixtureDef.shape=polygonShape;

                    body.CreateFixture(fixtureDef);
                }
            }

            return body;
        }

		
        public function PhysicsData(): void
		{
			dict = new Dictionary();
			

			dict["ship1"] = [

										[
											// density, friction, restitution
                                            2, 0, 0,
                                            // categoryBits, maskBits, groupIndex, isSensor
											1, 65535, 0, false,
											'POLYGON',
											[

                                                [   new b2Vec2(58/ptm_ratio, 34.5/ptm_ratio)  ,  new b2Vec2(57.875/ptm_ratio, 29/ptm_ratio)  ,  new b2Vec2(63.5/ptm_ratio, 32/ptm_ratio)  ] ,
                                                [   new b2Vec2(54.125/ptm_ratio, 37.125/ptm_ratio)  ,  new b2Vec2(41.375/ptm_ratio, 22.625/ptm_ratio)  ,  new b2Vec2(54.75/ptm_ratio, 26.75/ptm_ratio)  ,  new b2Vec2(57.875/ptm_ratio, 29/ptm_ratio)  ,  new b2Vec2(58/ptm_ratio, 34.5/ptm_ratio)  ,  new b2Vec2(57.625/ptm_ratio, 37.75/ptm_ratio)  ] ,
                                                [   new b2Vec2(57.875/ptm_ratio, 29/ptm_ratio)  ,  new b2Vec2(54.75/ptm_ratio, 26.75/ptm_ratio)  ,  new b2Vec2(57.25/ptm_ratio, 25.875/ptm_ratio)  ] ,
                                                [   new b2Vec2(0/ptm_ratio, 44.5/ptm_ratio)  ,  new b2Vec2(0.125/ptm_ratio, 19.375/ptm_ratio)  ,  new b2Vec2(8/ptm_ratio, 19.75/ptm_ratio)  ,  new b2Vec2(11.5/ptm_ratio, 24.625/ptm_ratio)  ,  new b2Vec2(12/ptm_ratio, 38.5/ptm_ratio)  ,  new b2Vec2(8.25/ptm_ratio, 44.125/ptm_ratio)  ] ,
                                                [   new b2Vec2(41.25/ptm_ratio, 41.125/ptm_ratio)  ,  new b2Vec2(16.375/ptm_ratio, 38.375/ptm_ratio)  ,  new b2Vec2(41.375/ptm_ratio, 22.625/ptm_ratio)  ,  new b2Vec2(54.125/ptm_ratio, 37.125/ptm_ratio)  ] ,
                                                [   new b2Vec2(16.75/ptm_ratio, 24.625/ptm_ratio)  ,  new b2Vec2(16.375/ptm_ratio, 38.375/ptm_ratio)  ,  new b2Vec2(12/ptm_ratio, 38.5/ptm_ratio)  ,  new b2Vec2(11.5/ptm_ratio, 24.625/ptm_ratio)  ] ,
                                                [   new b2Vec2(16.375/ptm_ratio, 38.375/ptm_ratio)  ,  new b2Vec2(41.25/ptm_ratio, 41.125/ptm_ratio)  ,  new b2Vec2(30.5/ptm_ratio, 61/ptm_ratio)  ,  new b2Vec2(25/ptm_ratio, 64.25/ptm_ratio)  ] ,
                                                [   new b2Vec2(16.375/ptm_ratio, 38.375/ptm_ratio)  ,  new b2Vec2(16.75/ptm_ratio, 24.625/ptm_ratio)  ,  new b2Vec2(25/ptm_ratio, -0.5/ptm_ratio)  ,  new b2Vec2(30/ptm_ratio, 1.5/ptm_ratio)  ,  new b2Vec2(41.375/ptm_ratio, 22.625/ptm_ratio)  ]
											]
										]

									];

		}
	}
}
